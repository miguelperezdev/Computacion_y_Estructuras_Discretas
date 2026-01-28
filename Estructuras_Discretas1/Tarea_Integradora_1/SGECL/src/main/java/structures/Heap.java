package structures;

import interfaces.IHeap;
import model.Team;

/**
 * Implementation of a max-heap data structure for Team objects.
 * Teams are ordered by total points (descending) and, in case of ties,
 * by their coefficient (descending).
 *
 * The heap is implemented using an array with a fixed capacity of 36 elements.
 */

public class Heap implements IHeap {
    private Team[] heap;
    private int size;
    private static final int TEAM_CAPACITY = 36;

    public Heap(){
        heap = new Team[TEAM_CAPACITY];
        size = 0;

    }

    public Team[] getHeap() {
        return heap;
    }

    public void setHeap(Team[] heap) {
        this.heap = heap;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    /**
     * Inserts a team into the heap while maintaining the heap property.
     *
     * @param team the team to be inserted
     */
    public void insert(Team team){
        heap[size] = team;
        int current= size ;
        while(current > 0 && priorityTeam(heap[current], heap[parent(current)])){
            swap(current, parent(current));
            current = parent(current);
        }
        size++;
    }
    /**
     * Swaps two elements in the heap array.
     *
     * @param i index of the first element
     * @param j index of the second element
     */
    private void swap(int i, int j){
        Team temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }
    /**
     * Calculates the parent index for a given node index.
     *
     * @param current the child node index
     * @return the parent node index
     */
    private int parent(int current) {
        return (current - 1) / 2;

    }
    /**
     * Determines if team A has priority over team B based on:
     * 1. Total points (higher first)
     * 2. Coefficient (if points are equal)
     *
     * @param a first team to compare
     * @param b second team to compare
     * @return true if team a has priority over team b
     */
    public boolean priorityTeam(Team a, Team b){
        if(a.getTotalPoints() > b.getTotalPoints()){
            return true;
        }
        if(a.getTotalPoints() == b.getTotalPoints()){
            return a.getCoefficient() > b.getCoefficient();
        }
        return false;
    }
    /**
     * Removes and returns the team with highest priority (root of the heap).
     *
     * @return the team with highest priority, or null if heap is empty
     */
    @Override
    public Team extractMax() {
        if(isEmpty()){
            return null;
        }
        Team top = heap[0];
        heap[0] = heap[size-1];
        size--;
        heapifyDown(0);
        return top;

    }
    /**
     * Restores the heap property starting from a given index downward.
     *
     * @param i the index to start heapifying from
     */
    private void heapifyDown(int i){
        int wide = i;
        int left = left(i);
        int right = right(i);
        if(left < size && heap[left] != null && heap[wide] != null &&  priorityTeam(heap[left], heap[wide])){
            wide = left;
        }
        if(right < size && heap[right] != null && heap[wide] != null && priorityTeam(heap[right], heap[wide])){
            wide = right;
        }
        if(wide != i){
            swap(i, wide);
            heapifyDown(wide);
        }



    }
    /**
     * Gets the left child index for a given node index.
     *
     * @param i the parent node index
     * @return the left child index
     */
    private int left(int i){
        return 2 * i + 1;
    }
    /**
     * Gets the right child index for a given node index.
     *
     * @param i the parent node index
     * @return the right child index
     */
    private int right(int i){
        return 2 * i + 2;
    }

    /**
     * Returns (without removing) the team with highest priority.
     *
     * @return the team with highest priority, or null if heap is empty
     */
    @Override
    public Team peek() {
        if(isEmpty()){
            return null;
        }
        return heap[0];
    }
    /**
     * Checks if the heap is empty.
     *
     * @return true if the heap contains no elements
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }
    /**
     * Removes a specific team from the heap and restores the heap property.
     *
     * @param team the team to be removed
     */
    public void remove(Team team){
        int index = -1;
        for(int i = 0 ; i < size; i++){
            if(heap[i]!=null && heap[i].equals(team)){
                index = i;
                break;
            }
        }
        if(index == -1){
            return;
        }
        heap[index] = heap[size-1];
        heap[size -1] = null;
        size--;
        if(index > 0 && heap[index]!=null && heap[parent(index)]!= null && heap[index].compareTo(heap[parent(index)]) > 0){
            heapifyUp(index);
        }else{
            heapifyDown(index);
        }
    }
    /**
     * Restores the heap property starting from a given index upward.
     *
     * @param i the index to start heapifying from
     */
    public void heapifyUp(int i){
        while(i > 0 && heap[i] != null && heap[parent(i)]!= null && heap[i].compareTo(heap[parent(i)]) > 0){
            swap(i, parent(i));
            i = parent(i);
        }
    }
}
