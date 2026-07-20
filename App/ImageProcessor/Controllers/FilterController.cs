using Microsoft.AspNetCore.Mvc;

namespace ImageProcessor.Controllers
{
    [ApiController]
    [Route("/api/filter")]
    public class FilterController : ControllerBase
    {

        [HttpPost]
        public IFormFile Filter(IFormFile file)
        {
            return file;
        }

    }
}
