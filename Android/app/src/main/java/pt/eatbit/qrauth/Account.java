package pt.eatbit.qrauth;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static java.lang.Integer.parseInt;

/**
 * Created by sousa on 09-06-2017.
 */

public class Account implements Serializable {

    int id;
    String website;
    String tag;
    String username;
    String password;


    public Account (String website, String tag, String username, String password){
        this.website    = website;
        this.tag        = tag;
        this.username   = username;
        this.password   = password;
    }

    public String getName(){
        return website;
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
    public Iterator<Account> iterator() {
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

    public boolean add(Account account) {
        return false;
    }

    public boolean remove(Object o) {
        return false;
    }

    public boolean containsAll(@NonNull Collection<?> c) {
        return false;
    }


    public boolean addAll(@NonNull Collection<? extends Account> c) {
        return false;
    }

    public boolean addAll(int index, @NonNull Collection<? extends Account> c) {
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


    public Account get(int index) {
        return null;
    }

    public Account set(int index, Account element) {
        return null;
    }


    public void add(int index, Account element) {

    }


    public Account remove(int index) {
        return null;
    }


    public int indexOf(Object o) {
        return 0;
    }


    public int lastIndexOf(Object o) {
        return 0;
    }


    public ListIterator<Account> listIterator() {
        return null;
    }

    @NonNull

    public ListIterator<Account> listIterator(int index) {
        return null;
    }

    @NonNull

    public List<Account> subList(int fromIndex, int toIndex) {
        return null;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.website = name;
    }


}
