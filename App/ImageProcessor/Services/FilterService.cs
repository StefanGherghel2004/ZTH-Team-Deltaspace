using ImageProcessor.Filters;
using ImageProcessor.Models;
using System.Net.Http.Headers;
using System.Numerics;
using System.Threading.Tasks;
using SixLabors.ImageSharp;
using SixLabors.ImageSharp.PixelFormats;
using SixLabors.ImageSharp.Processing;

namespace ImageProcessor.Service
{
    public class FilterService
    {
        private readonly HttpClient _httpClient;

        public FilterService(HttpClient httpClient)
        {
            _httpClient = httpClient;
        }

        public async Task ProcessAndUploadImageAsync(ImageProcessingRequest request)
        {
            if (!Enum.TryParse<FilterType>(request.Filter, true, out var filterType))
            {
                throw new ArgumentException($"Filter {request.Filter} is not implemented.");
            }
            using var networkStream = await _httpClient.GetStreamAsync(request.DownloadUrl);
            var (image, originalFormat) = await Image.LoadWithFormatAsync<Rgba32>(networkStream);

            using (image)
            {
                ApplyFilter(image, filterType);

                using var outStream = new MemoryStream();
                await image.SaveAsync(outStream, originalFormat);

                outStream.Position = 0;
                using var content = new StreamContent(outStream);
                content.Headers.ContentType = new MediaTypeHeaderValue(originalFormat.DefaultMimeType);

                var response = await _httpClient.PutAsync(request.UploadUrl, content);
                response.EnsureSuccessStatusCode();
            }
        }

        private void ApplyFilter(Image<Rgba32> image, FilterType type)
        {
            switch (type)
            {
                case FilterType.Grayscale:
                    image.Mutate(ctx => ctx.Grayscale());
                    break;

                case FilterType.Invert:
                    image.Mutate(ctx => ctx.Invert());
                    break;

                case FilterType.Sepia:
                    image.Mutate(ctx => ctx.Sepia());
                    break;

                case FilterType.Neon:
                    ApplyNeon(image);
                    break;

                case FilterType.Sketch:
                    ApplySketch(image);
                    break;

                case FilterType.Pixel:
                    ApplyPixel(image);
                    break;
            }
        }

        private static void ApplyNeon(Image<Rgba32> image)
        {
            image.Mutate(ctx => ctx.ProcessPixelRowsAsVector4(rowSpan =>
            {
                for (int i = 0; i < rowSpan.Length; i++)
                {
                    Vector4 v = rowSpan[i];

                    float lum = 0.299f * v.X + 0.587f * v.Y + 0.114f * v.Z;
                    float smoothLum = lum * lum * (3.0f - 2.0f * lum);

                    float finalR = (10f + smoothLum * 245f) / 255f;
                    float finalG = (150f * (1.0f - smoothLum) + 20f * smoothLum) / 255f;
                    float finalB = (230f * (1.0f - smoothLum) + 220f * smoothLum) / 255f;

                    float intensity = Math.Min(1.0f, lum * 2.0f);

                    rowSpan[i] = new Vector4(
                        Math.Clamp(finalR * intensity, 0f, 1f),
                        Math.Clamp(finalG * intensity, 0f, 1f),
                        Math.Clamp(finalB * intensity, 0f, 1f),
                        v.W
                    );
                }
            }));
        }

        private static void ApplySketch(Image<Rgba32> image)
        {
            // combines multiple effects to simulate a ink sketch effect
            image.Mutate(x => x
                .Grayscale()
                .GaussianBlur(1.0f)
                .GaussianSharpen(2.5f)
                .Contrast(2.0f)
                .DetectEdges()
                .Invert()
            );
        }

        private static void ApplyPixel(Image<Rgba32> image)
        {
            int origWidth = image.Width;
            int origHeight = image.Height;
            int scaleFactor = 24; // factor controlling the pixel size
            int gridWidth = Math.Max(8, origWidth / scaleFactor);
            int gridHeight = Math.Max(8, origHeight / scaleFactor);
            image.Mutate(x => x
                // shrink the image
                .Resize(new ResizeOptions
                {
                    Size = new Size(gridWidth, gridHeight),
                    Sampler = KnownResamplers.NearestNeighbor
                })
                // enlarge it back to original size to create a pixelated effect
                .Resize(new ResizeOptions
                {
                    Size = new Size(origWidth, origHeight),
                    Sampler = KnownResamplers.NearestNeighbor
                })
                .Saturate(1.7f)
                .Contrast(1.4f));
        }


    }
}