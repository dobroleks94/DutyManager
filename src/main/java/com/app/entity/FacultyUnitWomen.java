package com.app.entity;



public class FacultyUnitWomen extends FacultyUnit{

    private int unitNumb;

    public FacultyUnitWomen(){
        super();
        this.setWomen(null);
    }
    public FacultyUnitWomen(FacultyUnit unit) {
        super();
        this.unitNumb = unit.getUnitNumber();
        this.setWomen(null);
    }


    public int getUnitNumb() {
        return unitNumb;
    }

    @Override
    public String toString() {
        return "Women of " + unitNumb;
    }
}
