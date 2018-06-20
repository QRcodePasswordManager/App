package pt.eatbit.eatbit;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static java.lang.Integer.parseInt;

/**
 * Created by sousa on 09-06-2017.
 */

public class Restaurant {

    int id;
    String name;
    String description;
    String address;
    String phonenumber;
    String coord;
    float rating;
    String photo;
    String type;



    public String getName(){
        return name;
    }

    public int size() {
        return 0;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean contains(Object o) {
        return false;
    }

    @NonNull
    public Iterator<Restaurant> iterator() {
        return null;
    }

    @NonNull
    public Object[] toArray() {
        return new Object[0];
    }

    @NonNull
    public <T> T[] toArray(@NonNull T[] a) {
        return null;
    }

    public boolean add(Restaurant restaurant) {
        return false;
    }

    public boolean remove(Object o) {
        return false;
    }

    public boolean containsAll(@NonNull Collection<?> c) {
        return false;
    }


    public boolean addAll(@NonNull Collection<? extends Restaurant> c) {
        return false;
    }

    public boolean addAll(int index, @NonNull Collection<? extends Restaurant> c) {
        return false;
    }

    public boolean removeAll(@NonNull Collection<?> c) {
        return false;
    }

    public boolean retainAll(@NonNull Collection<?> c) {
        return false;
    }

    public void clear() {

    }


    public Restaurant get(int index) {
        return null;
    }

    public Restaurant set(int index, Restaurant element) {
        return null;
    }


    public void add(int index, Restaurant element) {

    }


    public Restaurant remove(int index) {
        return null;
    }


    public int indexOf(Object o) {
        return 0;
    }


    public int lastIndexOf(Object o) {
        return 0;
    }


    public ListIterator<Restaurant> listIterator() {
        return null;
    }

    @NonNull

    public ListIterator<Restaurant> listIterator(int index) {
        return null;
    }

    @NonNull

    public List<Restaurant> subList(int fromIndex, int toIndex) {
        return null;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void rating(float aFloat) {
        this.rating = aFloat;
    }

    public void hours(String string) {
    }

    public void coord(String string) {
        this.coord = string;
    }

    public void phonenumber(String string) {
        this.phonenumber = string;
    }

    public void type(String string) {
        this.type = string;
    }

    public void image(String string) {
        this.photo = string;
    }

    public void addr(String string) {
        this.address = string;
    }

    public String getPhoto() {
        return photo;
    }

    public String getDesc() {
        return description;
    }

    public float getRating() {
        return rating;
    }
}
