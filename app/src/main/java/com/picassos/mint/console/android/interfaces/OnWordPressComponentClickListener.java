package com.picassos.mint.console.android.interfaces;

import com.picassos.mint.console.android.models.wordpress.CategoryDesigns;
import com.picassos.mint.console.android.models.wordpress.PostDesigns;
import com.picassos.mint.console.android.models.wordpress.TagDesigns;

public interface OnWordPressComponentClickListener {
    void onPostClick(PostDesigns postDesigns);
    void onCategoryClick(CategoryDesigns categoryDesigns);
    void onTagClick(TagDesigns tagDesigns);
}
