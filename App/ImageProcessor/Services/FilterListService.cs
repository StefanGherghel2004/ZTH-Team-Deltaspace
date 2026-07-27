using ImageProcessor.Filters;

namespace ImageProcessor.Services
{
    public class FilterListService
    {
        private static readonly List<FilterDetails> _filters = new List<FilterDetails>
        {
            new FilterDetails { Id = (int)FilterType.None, Name = "none", Label = "No Filter" },
            new FilterDetails { Id = (int)FilterType.Grayscale, Name = "grayscale", Label = "Black and White" },
      
        };

        public List<FilterDetails> GetFilters()
        {
            return _filters;
        }
    }
}
