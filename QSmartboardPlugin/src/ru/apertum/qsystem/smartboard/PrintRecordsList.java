package ru.apertum.qsystem.smartboard;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import javax.swing.AbstractListModel;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.server.model.QOffice;
import ru.apertum.qsystem.server.model.QOfficeList;

/**
 * Keeps a list of all PrintRecords objects for each office. Uses the standard QSystem method of the
 * private Holder object to keep the list in the cache
 *
 * @author Sean Rumsby
 */
public class PrintRecordsList extends AbstractListModel implements List {

    private final List<PrintRecords> printRecords = new LinkedList<>();

    public PrintRecordsList() {
        QOfficeList officeList = QOfficeList.getInstance();

        for (QOffice office : officeList.getItems()) {
            QLog.l().logQUser().debug("Adding printRecords for office" + office);
            PrintRecords pr = new PrintRecords(office);

            this.printRecords.add(pr);
        }
    }

    public static PrintRecordsList getInstance() {
        return PrintRecordsList.PrintRecordsListHolder.INSTANCE;
    }

    public List<PrintRecords> getPrintRecords() {
        return printRecords;
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

    private static class PrintRecordsListHolder {

        private static final PrintRecordsList INSTANCE = new PrintRecordsList();
    }
}