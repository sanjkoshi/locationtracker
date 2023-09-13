package com.gewinns.hr;
public class Employee {
    private String employee_id;
    private String subdomain;
    private String user_id;
    Employee(String eid,String subdomain, String user) {
        this.employee_id = eid;
        this.subdomain = subdomain;
        this.user_id = user;
    }

    public String getEmployee_id() {
        return employee_id;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public String getUser_id() {
        return user_id;
    }
}