using ImageProcessor.Services;
using Microsoft.AspNetCore.Mvc;

namespace ImageProcessor.Controllers
{
    [ApiController]
    [Route("/filters")]
    public class FilterListController : ControllerBase
    {
        private readonly FilterListService _filterListService;

        public FilterListController(FilterListService filterListService)
        {
            _filterListService = filterListService;
        }

        [HttpGet]
        public IActionResult GetFilters()
        {
            try
            {
                var filters = _filterListService.GetFilters();
                return Ok(new { success = true, data = filters });
            }
            catch (Exception ex)
            {
                return StatusCode(500, ex.Message);
            }
        }
    }
}
