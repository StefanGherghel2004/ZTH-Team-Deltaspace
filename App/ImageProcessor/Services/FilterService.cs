using ImageProcessor.Filters;
using SixLabors.ImageSharp;
using SixLabors.ImageSharp.Formats.Png;
using SixLabors.ImageSharp.PixelFormats;

namespace ImageProcessor.Service
{

    public class FilterService
    {
        public async Task<byte[]> ProcessImageAsync(Stream inputStream, FilterType type)
        {
            using var image = await Image.LoadAsync<Rgba32>(inputStream);
            var pixelData = new byte[image.Width * image.Height * 4];
            image.CopyPixelDataTo(pixelData);

            applyFilter(pixelData, type);

            using var finalImage = Image.LoadPixelData<Rgba32>(pixelData, image.Width, image.Height);
            using var outStream = new MemoryStream();
            await finalImage.SaveAsync(outStream, new PngEncoder());
            return outStream.ToArray();
        }

        private void ApplyGrayscale(byte[] pixels)
        {
            for (int i = 0; i < pixels.Length; i += 4)
            {
                byte gray = (byte)((pixels[i] * 0.299) + (pixels[i + 1] * 0.587) + (pixels[i + 2] * 0.114));
                pixels[i] = gray;
                pixels[i + 1] = gray;
                pixels[i + 2] = gray;
            }
        }

        private void applyFilter(byte[] pixels, FilterType type)
        {
            switch (type)
            {
                case FilterType.Grayscale:
                    ApplyGrayscale(pixels);
                    break;
            }
        }
    }
}