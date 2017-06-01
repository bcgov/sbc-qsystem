/*
 *  Copyright (C) 2010 {Apertum}Projects. web: www.apertum.ru email: info@apertum.ru
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.apertum.qsky.model;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author egorov
 */
@Entity
@Table(name = "branch")
public class Branch extends Element {

    @Column(name = "branch_id", nullable = false, unique = true)
    private Long branchId;

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    @Column(name = "name", length = 255, nullable = false, columnDefinition = "")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Column(name = "active", columnDefinition = "false", nullable = false)
    private Boolean active = true;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
    
    @Column(name = "time_zone")
    private Integer timeZone = 0;

    public Integer getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(Integer timeZone) {
        this.timeZone = timeZone;
    }
    

    /**
     * The parent domain, can be null if this is the root domain.
     */
    @ManyToOne()
    @JoinColumn(name = "parent_id")
    private Branch parent;

    public Branch getParent() {
        return parent;
    }

    public void setParent(Branch parent) {
        this.parent = parent;
    }

    /**
     * The children domain of this domain.
     *
     * This is the inverse side of the parent relation.
     *
     * <strong>It is the children responsibility to manage there parents children set!</strong>
     */
    @OneToMany()
    @JoinColumn(name = "parent_id")
    private Set<Branch> children = new HashSet<>();

    /**
     * Do not use this Constructor! Used only by Hibernate.
     */
    public Branch() {
    }
    
    public Branch(String name) {
        this.name = name;
    }

    /**
     * Instantiates a new domain. The domain will be of the same state like the parent domain.
     *
     * @param parent the parent domain
     * @see Domain#createRoot()
     */
    public Branch(final Branch parent) {
        if (parent == null) {
            throw new IllegalArgumentException("parent required");
        }

        this.parent = parent;
        registerInParentsChilds();
    }

    /**
     * Register this domain in the child list of its parent.
     */
    private void registerInParentsChilds() {
        this.parent.children.add(this);
    }

    /**
     * Return the <strong>unmodifiable</strong> children of this domain.
     *
     * @return the child nodes.
     */
    public Set<Branch> getChildren() {
        return children;
    }

    /**
     * Move this domain to an new parent domain.
     *
     * @param newParent the new parent
     */
    public void move(final Branch newParent) {
        /*
         Check.notNullArgument(newParent, "newParent");

         if (!isProperMoveTarget(newParent)) { // detect circles... 
         throw new IllegalArgumentException("move", "not a proper new parent", this);
         }
         */

        this.parent.children.remove(this);
        this.parent = newParent;
        registerInParentsChilds();
    }

    /**
     * Creates the root.
     *
     * @return the domain
     */
    public static Branch createRoot() {
        return new Branch();
    }

    public boolean isParentOf(Branch br) {
        while (br.getParent() != null && !br.getParent().getId().equals(getId())) {
            br = br.getParent();
        }
        return br.getParent() != null;
    }

    @Override
    public String toString() {
        return name + " " + branchId.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
