package projekt.dashboard.util;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import projekt.dashboard.BuildConfig;

/**
 * @author Aidan Follestad (afollestad)
 */
public class DrawableXmlParser {

    private static Category mCurrentCategory;
    private static ArrayList<Category> mCategories;

    private DrawableXmlParser() {
    }

    public static void cleanup() {
        mCategories = null;
    }

    public static class Icon implements Serializable {

        static final int SPACE = 1;
        static final int CAPS = 2;
        static final int CAPS_LOCK = 3;
        private final String mDrawable;
        private final Category mCategory;
        private String mName;

        public Icon(String drawable, Category category) {
            mDrawable = drawable;
            mCategory = category;
            getName(); // generate name
        }

        public long getUniqueId() {
            return mName.hashCode();
        }

        public String getName() {
            if (mName != null || mDrawable == null) return mName;

            StringBuilder sb = new StringBuilder();
            int underscoreMode = 0;
            boolean foundFirstLetter = false;
            boolean lastWasLetter = false;

            for (int i = 0; i < mDrawable.length(); i++) {
                final char c = mDrawable.charAt(i);
                if (Character.isLetterOrDigit(c)) {
                    if (underscoreMode == SPACE) {
                        sb.append(' ');
                        underscoreMode = CAPS;
                    }
                    if (!foundFirstLetter && underscoreMode == CAPS)
                        sb.append(c);
                    else sb.append(i == 0 || underscoreMode > 1 ? Character.toUpperCase(c) : c);
                    if (underscoreMode < CAPS_LOCK)
                        underscoreMode = 0;
                    foundFirstLetter = true;
                    lastWasLetter = true;
                } else if (c == '_') {
                    if (underscoreMode == CAPS_LOCK) {
                        if (lastWasLetter) {
                            underscoreMode = SPACE;
                        } else {
                            sb.append(c);
                            underscoreMode = 0;
                        }
                    } else {
                        underscoreMode++;
                    }
                    lastWasLetter = false;
                }
            }

            mName = sb.toString();
            return mName;
        }

        public Category getCategory() {
            return mCategory;
        }

        public String getDrawable() {
            return mDrawable;
        }

        public int getDrawableId(Context context) {
            if (mDrawable == null)
                return 0;
            return context.getResources().getIdentifier(mDrawable, "drawable", BuildConfig.APPLICATION_ID);
        }

        @Override
        public String toString() {
            return getDrawable();
        }
    }

    public static class Category implements Serializable {

        private final String mName;
        private final ArrayList<Icon> mIcons;

        public Category(String name) {
            mName = name;
            mIcons = new ArrayList<>();
        }

        public String getName() {
            return mName;
        }

        public List<Icon> getIcons() {
            return mIcons;
        }

        public void addItem(Icon icon) {
            mIcons.add(icon);
        }

        public int size() {
            return mIcons.size();
        }

        @Override
        public String toString() {
            return String.format(Locale.getDefault(), "%s (%d)", mName, getIcons().size());
        }
    }
}