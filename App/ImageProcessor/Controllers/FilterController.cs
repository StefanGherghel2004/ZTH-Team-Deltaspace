using ImageProcessor.Filters;
using ImageProcessor.Service;
using Microsoft.AspNetCore.Mvc;

namespace ImageProcessor.Controllers
{
    [ApiController]
    [Route("/api/filter")]
    public class FilterController : ControllerBase
    {
        private readonly FilterService _filterService;

        public FilterController(FilterService filterService)
        {
            _filterService = filterService;
        }

        [HttpPost]
        public async Task<IActionResult> ApplyFilter(IFormFile imageFile, [FromForm] FilterType filter)
        {
            if (imageFile == null || imageFile.Length == 0) {
                return BadRequest("Image is null.");
            }

            try
            {
                using var stream = imageFile.OpenReadStream();
                var result = await _filterService.ProcessImageAsync(stream, filter);

                return File(result, "image/png");
            }
            catch (Exception ex)
            {
                return StatusCode(500, ex.Message);
            }
        }

    }
}
