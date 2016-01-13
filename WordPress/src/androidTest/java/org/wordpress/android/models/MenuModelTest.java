package org.wordpress.android.models;

import android.content.ContentValues;
import android.content.Context;
import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;
import android.test.mock.MockCursor;

import org.wordpress.android.TestUtils;
import org.wordpress.android.datasets.MenusTable;

import java.util.ArrayList;
import java.util.List;

public class MenuModelTest extends InstrumentationTestCase {
    private static final String TEST_ID = "MenuModelTest";
    private static final String TEST_NAME = "MenuModelTestName";
    private static final String TEST_DETAILS = "MenuModelTestDetails";
    private static final String TEST_LOCATIONS = "TESTLOC0,TESTLOC1,TESTLOC2,TESTLOC3,TESTLOC4";
    private static final String TEST_ITEMS = "TESTITEM0,TESTITEM1,TESTITEM2,TESTITEM3,TESTITEM4";

    protected Context mTestContext;
    protected Context mTargetContext;

    @Override
    protected void setUp() throws Exception {
        mTargetContext = new RenamingDelegatingContext(getInstrumentation().getTargetContext(), "test_");
        mTestContext = getInstrumentation().getContext();
        TestUtils.loadDBFromDump(mTargetContext, mTestContext, "taliwutt-blogs-sample.sql");
        super.setUp();
    }

    public void testInvalidMenu() {
        assertNull(MenusTable.getMenuForMenuId(""));
    }

    public void testSerialize() {
        MenuModel testModel = getTestModel();
        ContentValues values = testModel.serializeToDatabase();
        assertEquals(TEST_ID, values.getAsString(MenuModel.ID_COLUMN_NAME));
        assertEquals(TEST_NAME, values.getAsString(MenuModel.NAME_COLUMN_NAME));
        assertEquals(TEST_DETAILS, values.getAsString(MenuModel.DETAILS_COLUMN_NAME));
        assertEquals(TEST_LOCATIONS, values.getAsString(MenuModel.LOCATIONS_COLUMN_NAME));
        assertEquals(TEST_ITEMS, values.getAsString(MenuModel.ITEMS_COLUMN_NAME));
    }

    public void testDeserialize() {
        MenuModel testModel = MenuModel.deserializeFromDatabase(new TestCursor());
        assertEquals(TEST_ID, testModel.menuId);
        assertEquals(TEST_NAME, testModel.name);
        assertEquals(TEST_DETAILS, testModel.details);
        assertEquals(TEST_LOCATIONS, testModel.serializeMenuLocations());
        assertEquals(TEST_ITEMS, testModel.serializeMenuItems());
    }

    public void testEqualsWithNull() {
        //noinspection ObjectEqualsNull
        assertFalse(getTestModel().equals(null));
    }

    public void testEqualsWithSameMenu() {
        assertTrue(getTestModel().equals(getTestModel()));
    }

    public void testEqualsWithDifferentMenu() {
        MenuModel staticModel = getTestModel();
        MenuModel testModel = getTestModel();
        testModel.name = null;
        assertFalse(testModel.equals(staticModel));
        testModel.name = staticModel.name;
        testModel.menuId = null;
        assertFalse(testModel.equals(staticModel));
        testModel.menuId = staticModel.menuId;
        testModel.details = null;
        assertFalse(testModel.equals(staticModel));
        testModel.details = staticModel.details;
        testModel.locations = null;
        assertFalse(testModel.equals(staticModel));
        testModel.locations = staticModel.locations;
        testModel.menuItems = null;
        assertFalse(testModel.equals(staticModel));
    }

    private class TestCursor extends MockCursor {
        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public boolean moveToFirst() {
            return true;
        }

        @Override
        public String getString(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return TEST_ID;
                case 1:
                    return TEST_NAME;
                case 2:
                    return TEST_DETAILS;
                case 3:
                    return TEST_LOCATIONS;
                case 4:
                    return TEST_ITEMS;
                default:
                    return "";
            }
        }

        @Override
        public int getColumnIndex(String columnName) {
            switch (columnName) {
                case MenuModel.ID_COLUMN_NAME:
                    return 0;
                case MenuModel.NAME_COLUMN_NAME:
                    return 1;
                case MenuModel.DETAILS_COLUMN_NAME:
                    return 2;
                case MenuModel.LOCATIONS_COLUMN_NAME:
                    return 3;
                case MenuModel.ITEMS_COLUMN_NAME:
                    return 4;
                default:
                    return -1;
            }
        }
    }

    private MenuModel getTestModel() {
        MenuModel testModel = new MenuModel();
        testModel.menuId = TEST_ID;
        testModel.name = TEST_NAME;
        testModel.details = TEST_DETAILS;
        testModel.locations = getTestLocations();
        testModel.menuItems = getTestItems();
        return testModel;
    }

    private List<MenuLocationModel> getTestLocations() {
        List<MenuLocationModel> locations = new ArrayList<>();
        for (String name : TEST_LOCATIONS.split(",")) {
            locations.add(MenuLocationModel.fromName(name));
        }
        return locations;
    }

    private List<MenuItemModel> getTestItems() {
        List<MenuItemModel> items = new ArrayList<>();
        for (String id : TEST_ITEMS.split(",")) {
            items.add(MenuItemModel.fromItemId(id));
        }
        return items;
    }
}
