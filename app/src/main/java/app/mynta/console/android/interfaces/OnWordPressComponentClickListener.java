package app.mynta.console.android.interfaces;

import app.mynta.console.android.models.wordpress.CategoryDesigns;
import app.mynta.console.android.models.wordpress.PostDesigns;
import app.mynta.console.android.models.wordpress.TagDesigns;

public interface OnWordPressComponentClickListener {
    void onPostClick(PostDesigns postDesigns);
    void onCategoryClick(CategoryDesigns categoryDesigns);
    void onTagClick(TagDesigns tagDesigns);
}
