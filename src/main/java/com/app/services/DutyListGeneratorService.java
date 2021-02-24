package com.app.services;

import com.app.controller.MainPageController;
import com.app.entity.FacultyDutyInfo;
import com.app.entity.FacultyUnit;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import pl.jsolve.templ4docx.core.Docx;
import pl.jsolve.templ4docx.core.VariablePattern;
import pl.jsolve.templ4docx.variable.TextVariable;
import pl.jsolve.templ4docx.variable.Variables;

public class DutyListGeneratorService {
    public DutyListGeneratorService() {
    }

    public static Path generateDutyList(List<FacultyUnit> units, FacultyDutyInfo dutyInfo) throws URISyntaxException {
        ClassLoader classLoader = DutyListGeneratorService.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("dutytempl/duty_list_template.docx");

        assert inputStream != null;

        Docx template = new Docx(inputStream);
        template.setVariablePattern(new VariablePattern("#{", "}"));
        Variables variables = createVariablesForTempl(units, dutyInfo);
        template.fillTemplate(variables);
        Path pathToFile = Utils.saveFile(template, MainPageController.getMainPageStage(), dutyInfo);

        return pathToFile;
    }

    private static Variables createVariablesForTempl(List<FacultyUnit> units, FacultyDutyInfo dutyInfo) {
        Variables variables = new Variables();
        variables.addTextVariable(new TextVariable("#{faculty_numb}", FacultyDutyInfo.getFaculty().getNumber().toString()));
        variables.addTextVariable(new TextVariable("#{duty_date}", dutyInfo.getDutyDate()));
        variables.addTextVariable(new TextVariable("#{duty_time}", dutyInfo.getDutyTime()));
        variables.addTextVariable(new TextVariable("#{rank}", dutyInfo.getDutyPerson().getRank()));
        variables.addTextVariable(new TextVariable("#{name}", dutyInfo.getDutyPerson().getFullName()));

        for(int i = 5; i > 0; --i) {
            iteratedVariables(i, variables, (FacultyUnit)units.get(units.size() - i), dutyInfo);
        }

        variables.addTextVariable(new TextVariable("#{vac_names}", Utils.buildAdditionalDataString(units, "відп", dutyInfo.getWomenAdj())));
        variables.addTextVariable(new TextVariable("#{leave_names}", Utils.buildAdditionalDataString(units, "зв-ня", dutyInfo.getWomenAdj())));
        variables.addTextVariable(new TextVariable("#{detach_names}", Utils.buildAdditionalDataString(units, "відр", dutyInfo.getWomenAdj())));
        addGeneralUnitSummary(variables, units, dutyInfo);
        return variables;
    }

    private static void addGeneralUnitSummary(Variables variables, List<FacultyUnit> units, FacultyDutyInfo dutyInfo) {
        int all = units.stream().map(FacultyUnit::getGeneralCadetCount).reduce(0, Integer::sum);
        int present = units.stream().map(FacultyDataService::calculatePresentCadets).map(Integer::parseInt).reduce(0, Integer::sum);
        int duty = units.stream().map(FacultyUnit::getDutyCadets).map(List::size).reduce(0, Integer::sum);
        int ill = units.stream().map(FacultyUnit::getIllCadets).map(List::size).reduce(0, Integer::sum);
        String hosp = FacultyDataService.getHospitalRepresentation(units);
        int vac = units.stream().map(FacultyUnit::getVacationsCount).reduce(0, Integer::sum);
        int leave = units.stream().map(FacultyUnit::getOnLeaveCount).reduce(0, Integer::sum);
        String other = FacultyDataService.getOtherCadetInfo(units);
        int allWomen = units.stream().map(FacultyUnit::getWomen).map(FacultyUnit::getGeneralCadetCount).reduce(0, Integer::sum);
        int presentW = units.stream().map(FacultyUnit::getWomen).map(FacultyDataService::calculatePresentCadets).map(Integer::parseInt).reduce(0, Integer::sum);
        int dutyW = units.stream().map(FacultyUnit::getWomen).map(FacultyUnit::getDutyCadets).map(List::size).reduce(0, Integer::sum);
        int illW = units.stream().map(FacultyUnit::getWomen).map(FacultyUnit::getIllCadets).map(List::size).reduce(0, Integer::sum);
        String hospW = FacultyDataService.getHospitalRepresentation(units.stream().map(FacultyUnit::getWomen).collect(Collectors.toList()));
        int vacW = units.stream().map(FacultyUnit::getWomen).map(FacultyUnit::getVacationsCount).reduce(0, Integer::sum);
        int leaveW = units.stream().map(FacultyUnit::getWomen).map(FacultyUnit::getOnLeaveCount).reduce(0, Integer::sum);
        String otherW = FacultyDataService.getOtherCadetInfo(units.stream().map(FacultyUnit::getWomen).collect(Collectors.toList()));
        String allRepres;
        String presentRepres;
        String dutyRepres;
        String illRepres;
        String hospRepres;
        String vacRepres;
        String leaveRepres;
        String otherRepres;
        switch(dutyInfo.getWomenAdj()) {
            case 1:
                allRepres = String.format("%d (%d)", all, allWomen);
                presentRepres = String.format("%d (%d)", present - allWomen + presentW, presentW);
                dutyRepres = dutyW > 0 ? String.format("%d (%d)", duty + dutyW, dutyW) : String.valueOf(duty);
                illRepres = illW > 0 ? String.format("%d (%d)", ill + illW, illW) : String.valueOf(ill);
                hospRepres = !hospW.equals("") ? FacultyDataService.processHospRepres(hosp, hospW, false, false) : hosp;
                vacRepres = vacW > 0 ? String.format("%d (%d)", vac + vacW, vacW) : String.valueOf(vac);
                leaveRepres = leaveW > 0 ? String.format("%d (%d)", leave + leaveW, leaveW) : String.valueOf(leave);
                otherRepres = !otherW.equals("") ? FacultyDataService.processOtherRepres(other, otherW, dutyInfo.getWomenAdj()) : other;
                break;
            case 2:
                allRepres = String.format("%d", all - allWomen);
                presentRepres = String.format("%d", present - allWomen);
                dutyRepres = String.valueOf(duty);
                illRepres = String.valueOf(ill);
                hospRepres = hosp;
                vacRepres = String.valueOf(vac);
                leaveRepres = String.valueOf(leave);
                otherRepres = other;
                break;
            default:
                allRepres = String.valueOf(all);
                presentRepres = String.valueOf(present - allWomen + presentW);
                dutyRepres = String.valueOf(duty + dutyW);
                illRepres = String.valueOf(ill + illW);
                hospRepres = FacultyDataService.processHospRepres(hosp, hospW, false, true);
                vacRepres = String.valueOf(vac + vacW);
                leaveRepres = String.valueOf(leave + leaveW);
                otherRepres = FacultyDataService.processOtherRepres(other, otherW, dutyInfo.getWomenAdj());
        }

        variables.addTextVariable(new TextVariable("#{all}", allRepres));
        variables.addTextVariable(new TextVariable("#{pres}", presentRepres));
        variables.addTextVariable(new TextVariable("#{duty}", dutyRepres));
        variables.addTextVariable(new TextVariable("#{ill}", illRepres));
        variables.addTextVariable(new TextVariable("#{hosp}", hospRepres));
        variables.addTextVariable(new TextVariable("#{vac}", vacRepres));
        variables.addTextVariable(new TextVariable("#{leave}", leaveRepres));
        variables.addTextVariable(new TextVariable("#{other}", otherRepres));
    }

    private static void iteratedVariables(int iteration, Variables variables, FacultyUnit unit, FacultyDutyInfo dutyInfo) {
        int all = unit.getGeneralCadetCount();
        int present = Integer.parseInt(FacultyDataService.calculatePresentCadets(unit)) - unit.getWomen().getGeneralCadetCount() + Integer.parseInt(FacultyDataService.calculatePresentCadets(unit.getWomen()));
        int duty = unit.getDutyCadets().size();
        int ill = unit.getIllCadets().size();
        String hosp = FacultyDataService.getHospitalRepresentation(unit, "count");
        int vac = unit.getVacationsCount();
        int leave = unit.getOnLeaveCount();
        Map<String, Integer> other = new HashMap(unit.getOtherAbsentCadets());
        if (unit.getDetachedCount() != 0) {
            other.putIfAbsent("відр", unit.getDetachedCount());
        }

        int allWomen = unit.getWomen().getGeneralCadetCount();
        int presentW = Integer.parseInt(FacultyDataService.calculatePresentCadets(unit.getWomen()));
        int dutyW = unit.getWomen().getDutyCadets().size();
        int illW = unit.getWomen().getIllCadets().size();
        String hospW = FacultyDataService.getHospitalRepresentation(unit.getWomen(), "count");
        int vacW = unit.getWomen().getVacationsCount();
        int leaveW = unit.getWomen().getOnLeaveCount();
        Map<String, Integer> otherW = new HashMap(unit.getWomen().getOtherAbsentCadets());
        if (unit.getWomen().getDetachedCount() != 0) {
            otherW.putIfAbsent("відр", unit.getWomen().getDetachedCount());
        }

        String allRepres;
        String presentRepres;
        String dutyRepres;
        String illRepres;
        String hospRepres;
        String vacRepres;
        String leaveRepres;
        String otherRepres;
        String dutyNames;
        String illNames;
        String hospNames;
        switch(dutyInfo.getWomenAdj()) {
            case 1:
                allRepres = String.format("%d (%d)", all, allWomen);
                presentRepres = String.format("%d (%d)", present, presentW);
                dutyRepres = dutyW > 0 ? String.format("%d (%d)", duty + dutyW, dutyW) : String.valueOf(duty);
                illRepres = illW > 0 ? String.format("%d (%d)", ill + illW, illW) : String.valueOf(ill);
                hospRepres = !hospW.equals("") ? FacultyDataService.processHospRepres(hosp, hospW, false, false) : hosp;
                vacRepres = vacW > 0 ? String.format("%d (%d)", vac + vacW, vacW) : String.valueOf(vac);
                leaveRepres = leaveW > 0 ? String.format("%d (%d)", leave + leaveW, leaveW) : String.valueOf(leave);
                otherRepres = otherW.size() > 0 ? FacultyDataService.processOtherRepres(other, otherW, dutyInfo.getWomenAdj()) : Utils.trimMap(other);
                dutyNames = unit.getWomen().getDutyCadets().size() > 0 ? Utils.trimList(unit.getDutyCadets()) + String.format((unit.getDutyCadets().size() > 0 ? ", " : "") + "(%s)", Utils.trimList(unit.getWomen().getDutyCadets())) : Utils.trimList(unit.getDutyCadets());
                illNames = unit.getWomen().getIllCadets().size() > 0 ? Utils.trimList(unit.getIllCadets()) + String.format((unit.getIllCadets().size() > 0 ? ", " : "") + "(%s)", Utils.trimList(unit.getWomen().getIllCadets())) : Utils.trimList(unit.getIllCadets());
                hospNames = !hospW.equals("") ? FacultyDataService.processHospRepres(FacultyDataService.getHospitalRepresentation(unit, "names"), FacultyDataService.getHospitalRepresentation(unit.getWomen(), "names"), true, false) : FacultyDataService.getHospitalRepresentation(unit, "names");
                break;
            case 2:
                allRepres = String.format("%d", all - allWomen);
                presentRepres = String.format("%d", present - presentW);
                dutyRepres = String.valueOf(duty);
                illRepres = String.valueOf(ill);
                hospRepres = hosp;
                vacRepres = String.valueOf(vac);
                leaveRepres = String.valueOf(leave);
                otherRepres = Utils.trimMap(unit.getOtherAbsentCadets());
                dutyNames = Utils.trimList(unit.getDutyCadets());
                illNames = Utils.trimList(unit.getIllCadets());
                hospNames = FacultyDataService.getHospitalRepresentation(unit, "names");
                break;
            default:
                allRepres = String.valueOf(all);
                presentRepres = String.valueOf(present);
                dutyRepres = String.valueOf(duty + dutyW);
                illRepres = String.valueOf(ill + illW);
                hospRepres = FacultyDataService.processHospRepres(hosp, hospW, false, true);
                vacRepres = String.valueOf(vac + vacW);
                leaveRepres = String.valueOf(leave + leaveW);
                otherRepres = FacultyDataService.processOtherRepres(other, otherW, dutyInfo.getWomenAdj());
                dutyNames = unit.getWomen().getDutyCadets().size() > 0 ? Utils.trimList(unit.getDutyCadets()) + (unit.getDutyCadets().size() > 0 ? ", " : "") + Utils.trimList(unit.getWomen().getDutyCadets()) : Utils.trimList(unit.getDutyCadets());
                illNames = unit.getWomen().getIllCadets().size() > 0 ? Utils.trimList(unit.getIllCadets()) + (unit.getIllCadets().size() > 0 ? ", " : "") + Utils.trimList(unit.getWomen().getIllCadets()) : Utils.trimList(unit.getIllCadets());
                hospNames = FacultyDataService.processHospRepres(FacultyDataService.getHospitalRepresentation(unit, "names"), FacultyDataService.getHospitalRepresentation(unit.getWomen(), "names"), true, true);
        }

        variables.addTextVariable(new TextVariable("#{" + iteration + "}", String.valueOf(unit.getUnitNumber())));
        variables.addTextVariable(new TextVariable("#{all" + iteration + "}", allRepres));
        variables.addTextVariable(new TextVariable("#{pres" + iteration + "}", presentRepres));
        variables.addTextVariable(new TextVariable("#{duty" + iteration + "}", unit.getDutyCadets().size() <= 0 && unit.getWomen().getDutyCadets().size() <= 0 ? "" : dutyRepres));
        variables.addTextVariable(new TextVariable("#{ill" + iteration + "}", unit.getIllCadets().size() <= 0 && unit.getWomen().getIllCadets().size() <= 0 ? "" : illRepres));
        variables.addTextVariable(new TextVariable("#{hosp" + iteration + "}", hospRepres));
        variables.addTextVariable(new TextVariable("#{vac" + iteration + "}", unit.getVacationsCount() <= 0 && unit.getWomen().getVacationsCount() <= 0 ? "" : vacRepres));
        variables.addTextVariable(new TextVariable("#{leave" + iteration + "}", unit.getOnLeaveCount() <= 0 && unit.getWomen().getOnLeaveCount() <= 0 ? "" : leaveRepres));
        variables.addTextVariable(new TextVariable("#{other" + iteration + "}", otherRepres));
        variables.addTextVariable(new TextVariable("#{duty_names" + iteration + "}", dutyNames));
        variables.addTextVariable(new TextVariable("#{ill_names" + iteration + "}", illNames));
        variables.addTextVariable(new TextVariable("#{hosp_names" + iteration + "}", hospNames));
    }
}
