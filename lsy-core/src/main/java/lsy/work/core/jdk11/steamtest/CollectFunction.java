package lsy.work.core.jdk11.steamtest;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CollectFunction {
    public static void main(String[] args) {
        Map<String, List<Employee>> empSalaryGt1900GroupedByDept = Employee.employees()
                .stream()
                .filter(e -> e.getSalary() > 1900)
                .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.toList()));
        System.out.println(empSalaryGt1900GroupedByDept);

        Map<String, List<Employee>> empGroupedByDeptWithSalaryGt1900 = Employee.employees()
                .stream()
                .collect(Collectors.groupingBy(Employee::getDepartment,
                        Collectors.filtering(e -> e.getSalary() > 1900.00, Collectors.toList())));
        System.out.println(empGroupedByDeptWithSalaryGt1900);


        Map<String, Set<List<String>>> langByDept = Employee.employees()
                .stream()
                .collect(Collectors.groupingBy(Employee::getDepartment,
                        Collectors.mapping(Employee::getSpokenLanguages, Collectors.toSet())));
        System.out.println(langByDept);

        Map<String,Set<String>> langByDept2 = Employee.employees()
                .stream()
                .collect(Collectors.groupingBy(Employee::getDepartment,
                        Collectors.flatMapping(e -> e.getSpokenLanguages().stream(), Collectors.toSet())));
        System.out.println(langByDept2);
    }
}
