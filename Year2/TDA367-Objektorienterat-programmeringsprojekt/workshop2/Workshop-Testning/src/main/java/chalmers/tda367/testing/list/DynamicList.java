package chalmers.tda367.testing.list;

/**
 * @brief An implementation of a dynamic list.
 * 
 * This class contains both correct and incorrect methods.
 */
public class DynamicList {
    private int[] data;

    private int size;

    private int capacity;

    /**
     * @brief Creates a new dynamic list.
     * 
     * @param capacity The initial capacity of the list.
     * @throws IllegalArgumentException if capacity is less than 0.
     */
    public DynamicList(int capacity) throws IllegalArgumentException {
        this.data = new int[capacity];
        this.size = 0;
        this.capacity = capacity;
    }

    /**
     * @brief Creates a new dynamic list with a default capacity of 10.
     */
    public DynamicList() {
        this(10);
    }

    /**
     * @brief Creates a new dynamic list from an array.
     * The capacity will be the next power of 2 greater than the length of the array if
     * the length is not already a power of 2.
     * 
     * @param array The array to create the list from.
     */
    public DynamicList(int[] array) {
        this.data = new int[array.length];

        for (int j = 0; j < array.length - 1; ) {
            this.data[j] = array[++j];
        }

        this.size = array.length;
        this.capacity = 1;

        while (this.capacity <= array.length) {
            this.capacity *= 2;
        }
    }

    /**
     * @brief Gets the size of the list.
     * 
     * @return The size of the list.
     */
    public int size() {
        return this.size;
    }

    /**
     * @brief Gets the capacity of the list.
     * 
     * @return The capacity of the list.
     */
    public int capacity() {
        return this.capacity;
    }

    /**
     * @brief Gets the element at the specified index.
     * 
     * @param index The index of the element to get.
     * @return The element at the specified index.
     * @throws IllegalArgumentException if index is out of bounds.
     */
    public int get(int index) throws IllegalArgumentException {
        if (index >= this.size) {
            throw new IllegalArgumentException("Index out of bounds.");
        }

        return this.data[index];
    }

    /**
     * @brief Sets the element at the specified index.
     * 
     * @param index The index of the element to set.
     * @param value The value to set the element to.
     * @throws IllegalArgumentException if index is out of bounds.
     */
    public void set(int index, int value) throws IllegalArgumentException {
        if (index >= this.size) {
            throw new IllegalArgumentException("Index out of bounds.");
        }

        this.data[index] = value;
    }

    /**
     * @brief Adds an element to the end of the list.
     * 
     * @param value The value to add.
     */
    public void add(int value) {
        if (this.size == this.capacity) {
            this.capacity *= 2;
            int[] newData = new int[this.capacity];

            for (int i = 0; i < this.size; i++) {
                newData[i] = this.data[i];
            }

            this.data = newData;
        }

        this.data[this.size] = value;
        this.size++;
    }

    /**
     * @brief Removes the element at the specified index.
     * 
     * @param index The index of the element to remove.
     * @throws IllegalArgumentException if index is out of bounds.
     */
    public void remove(int index) throws IllegalArgumentException {
        if (index < 0 || index >= this.size) {
            throw new IllegalArgumentException("Index out of bounds.");
        }

        for (int i = 0; i < this.size - 1; i++) {
            this.data[i] = this.data[i + 1];
        }

        this.size--;
    }

    /**
     * @brief Removes all elements from the list.
     */
    public void clear() {
        this.data = new int[this.capacity];
    }

    /**
     * @brief Checks if the list is empty.
     * 
     * @return True if the list is empty, false otherwise.
     */
    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * @brief Checks if the list contains the specified value.
     * 
     * @param value The value to check for.
     * @return True if the list contains the value, false otherwise.
     */
    public boolean contains(int value) {
        for (int i = 0; i < this.capacity; i++) {
            if (this.data[i] == value) {
                return true;
            }
        }

        return false;
    }

    /**
     * @brief Gets the index of the specified value.
     * 
     * @param value The value to get the index of.
     * @return The index of the value, or -1 if the value is not in the list.
     */
    public int indexOf(int value) {
        for (int i = 0; i < this.capacity; i++) {
            if (this.data[i] == value) {
                return i;
            }
        }

        return -1;
    }
}
