/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.ub485.core;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Evgeniy Egorov
 */
public class UserTableModel extends AbstractTableModel {

    private final AddrProp props;

    UserTableModel(AddrProp instance) {
        props = instance;
    }

    @Override
    public int getRowCount() {
        return props.getAddrs().size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Long userId = props.getAddrs().keySet().toArray(new Long[0])[rowIndex];
        switch (columnIndex) {
            case 0:
                return props.getAddr(userId);
            case 1:
                return props.getAddr(userId).addres;
            case 2:
                return props.getAddr(userId).serveceName == null ? (props.getAddr(userId).redirectServiceId == null || props.getAddr(userId).redirectServiceId == 0 ? "нет" : props.getAddr(userId).redirectServiceId.toString()) : props.getAddr(userId).serveceName;
            case 3:
                return props.getAddr(userId).qsize;
            case 4:
                if (props.getAddr(userId).getUser() == null) {
                    return "Неизвестно";
                }
                if (props.getAddr(userId).getUser().getShadow() == null || props.getAddr(userId).getUser().getShadow().getCustomerState() == null) {
                    return "Не работает";
                } else {
                    switch (props.getAddr(userId).getUser().getShadow().getCustomerState()) {
                        case STATE_INVITED:
                            return "Вызван";
                        case STATE_INVITED_SECONDARY:
                            return "Вызван по этапу";
                        case STATE_WORK:
                            return "В работе";
                        case STATE_WORK_SECONDARY:
                            return "В работе по этапу";
                        case STATE_DEAD:
                            return "Откланен";
                        case STATE_FINISH:
                            return "Закончил работу";
                        case STATE_BACK:
                            return "Возвращен";
                        case STATE_REDIRECT:
                            return "Перенаправлен";
                        case STATE_WAIT:
                            return "Ожидает";
                        case STATE_WAIT_AFTER_POSTPONED:
                            return "После отложения";
                        default:
                            throw new AssertionError();
                    }
                }
            default:
                throw new AssertionError();
        }
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Оператор";
            case 1:
                return "Адрес RS485";
            case 2:
                return "Переадресация";
            case 3:
                return "Очередь";
            case 4:
                return "Состояние";
            default:
                throw new AssertionError();
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return ButtonDevice.class;
            case 1:
                return Byte.class;
            case 2:
                return String.class;
            case 3:
                return Integer.class;
            case 4:
                return String.class;
            default:
                throw new AssertionError();
        }
    }
}
