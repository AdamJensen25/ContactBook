package ua.vladprischepa.contactbooktesttask;

import java.util.List;

/**
 * Interface describes Selection Behavior for RecyclerView adapter
 *
 * @author Vlad Prischepa
 * @since 03.04.2017
 * @version 1
 */

public interface MultiSelector<T> {

    /**
     * Method for toggle selected item
     * @param position selected item's position
     */
    void toggleSelection(int position);

    /**
     * Method clears List of selected items
     */
    void clearSelection();

    /**
     * Method returns count of selected items
     * @return integer value of selected items count
     */
    int getSelectedItemsCount();

    /**
     * Method returns List of selected items
     * @return List with selected items
     */
    List<T> getSelectedItems();
}
