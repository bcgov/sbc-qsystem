/*
 * Copyright (C) 2015 Evgeniy Egorov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.apertum.qsystem.smartboard;

import ru.apertum.qsystem.server.model.QOffice;
import ru.apertum.qsystem.server.model.QOfficeList;

import javax.swing.*;
import java.util.*;

/**
 *
 * @author Sean Rumsby
 */
public class PrintRecordsList extends AbstractListModel implements List {

    public static PrintRecordsList getInstance() {
        return PrintRecordsList.PrintRecordsListHolder.INSTANCE;
    }

    private static class PrintRecordsListHolder {
        private static final PrintRecordsList INSTANCE = new PrintRecordsList();
    }

    private final List<PrintRecords> printRecords = new LinkedList<>();

    public List<PrintRecords> getPrintRecords() {
        return printRecords;
    }

    public PrintRecordsList() {
        QOfficeList officeList = QOfficeList.getInstance();

        for (QOffice office: officeList.getItems()) {
            PrintRecords pr = new PrintRecords(office);

            this.printRecords.add(pr);
        }
    }

    @Override
    public int getSize() {
        return printRecords.size();
    }

    @Override
    public Object getElementAt(int index) {
        return printRecords.get(index);
    }

    public boolean removeElement(PrintRecords obj) {
        final int index = printRecords.indexOf(obj);
        final boolean res = printRecords.remove(obj);
        fireIntervalRemoved(this, index, index);
        return res;
    }

    public void addElement(PrintRecords obj) {
        final int index = printRecords.size();
        printRecords.add(obj);
        fireIntervalAdded(this, index, index);
    }

    @Override
    public int size() {
        return getSize();
    }

    @Override
    public boolean isEmpty() {
        return getSize() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return printRecords.contains(o);
    }

    @Override
    public Iterator iterator() {
        return printRecords.iterator();
    }

    @Override
    public PrintRecords[] toArray() {
        return (PrintRecords[]) printRecords.toArray();
    }

    @Override
    public boolean add(Object e) {
        return printRecords.add((PrintRecords) e);
    }

    @Override
    public boolean remove(Object o) {
        return printRecords.remove((PrintRecords) o);
    }

    @Override
    public boolean containsAll(Collection c) {
        return printRecords.containsAll(c);
    }

    @Override
    public boolean addAll(Collection c) {
        return printRecords.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection c) {
        return printRecords.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection c) {
        return printRecords.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection c) {
        return printRecords.retainAll(c);
    }

    @Override
    public void clear() {
        printRecords.clear();
    }

    @Override
    public PrintRecords get(int index) {
        return printRecords.get(index);
    }

    @Override
    public PrintRecords set(int index, Object element) {
        return printRecords.set(index, (PrintRecords) element);
    }

    @Override
    public void add(int index, Object element) {
        printRecords.add(index, (PrintRecords) element);
    }

    @Override
    public PrintRecords remove(int index) {
        return printRecords.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return printRecords.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return printRecords.lastIndexOf(o);
    }

    @Override
    public ListIterator listIterator() {
        return printRecords.listIterator();
    }

    @Override
    public ListIterator listIterator(int index) {
        return printRecords.listIterator(index);
    }

    @Override
    public List subList(int fromIndex, int toIndex) {
        return printRecords.subList(fromIndex, toIndex);
    }

    @Override
    public PrintRecords[] toArray(Object[] a) {
        return (PrintRecords[]) printRecords.toArray(a);
    }
}