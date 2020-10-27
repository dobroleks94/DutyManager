package com.app.services;

import com.app.controller.MainPageController;
import com.app.entity.FacultyDutyInfo;
import com.app.entity.FacultyUnit;
import pl.jsolve.templ4docx.core.Docx;
import pl.jsolve.templ4docx.core.VariablePattern;
import pl.jsolve.templ4docx.variable.TextVariable;
import pl.jsolve.templ4docx.variable.Variables;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

public class DutyListGeneratorService {

    public static void generateDutyList(List<FacultyUnit> units, FacultyDutyInfo dutyInfo) throws URISyntaxException {
        ClassLoader classLoader = DutyListGeneratorService.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("dutytempl/duty_list_template.docx");
        assert inputStream != null;
        Docx template = new Docx(inputStream);
        //Docx template = new Docx(Objects.requireNonNull(DutyListGeneratorService.class.getClassLoader().getResource("dutytempl/duty_list_template.docx")).getPath());
        template.setVariablePattern(new VariablePattern("#{", "}"));

        Variables variables = createVariablesForTempl(units, dutyInfo);

        template.fillTemplate(variables);

        Utils.saveFile(template, MainPageController.getMainPageStage());
    }

    private static Variables createVariablesForTempl(List<FacultyUnit> units, FacultyDutyInfo dutyInfo) {
        Variables variables = new Variables();

        variables.addTextVariable(new TextVariable("#{faculty_numb}", FacultyDutyInfo.getFaculty().getNumber().toString()));
        variables.addTextVariable(new TextVariable("#{duty_date}", dutyInfo.getDutyDate()));
        variables.addTextVariable(new TextVariable("#{duty_time}", dutyInfo.getDutyTime()));
        variables.addTextVariable(new TextVariable("#{rank}", dutyInfo.getDutyPerson().getRank()));
        variables.addTextVariable(new TextVariable("#{name}", dutyInfo.getDutyPerson().getFullName()));

        for(int i = 5; i >0; i--)
            iteratedVariables(i, variables, units.get(units.size() - i));

        variables.addTextVariable(new TextVariable("#{vac_names}", Utils.buildAdditionalDataString(units, "відп")));
        variables.addTextVariable(new TextVariable("#{leave_names}", Utils.buildAdditionalDataString(units, "зв-ня")));
        variables.addTextVariable(new TextVariable("#{detach_names}", Utils.buildAdditionalDataString(units, "відр")));

        addGeneralUnitSummary(variables, units);


        return variables;
    }

    private static void addGeneralUnitSummary(Variables variables, List<FacultyUnit> units) {
        int all = units.stream().map(FacultyUnit::getGeneralCadetCount).reduce(0, Integer::sum);
        int present = units.stream().map(FacultyDataService::calculatePresentCadets).map(Integer::parseInt).reduce(0, Integer::sum);
        int duty = units.stream().map(FacultyUnit::getDutyCadets).map(List::size).reduce(0, Integer::sum);
        int ill = units.stream().map(FacultyUnit::getIllCadets).map(List::size).reduce(0, Integer::sum);
        String hosp = FacultyDataService.getHospitalRepresentation(units);
        int vac = units.stream().map(FacultyUnit::getVacationsCount).reduce(0, Integer::sum);
        int leave = units.stream().map(FacultyUnit::getOnLeaveCount).reduce(0, Integer::sum);
        String other = FacultyDataService.getOtherCadetInfo(units);

        variables.addTextVariable(new TextVariable("#{all}", String.valueOf(all)));
        variables.addTextVariable(new TextVariable("#{pres}", String.valueOf(present)));
        variables.addTextVariable(new TextVariable("#{duty}", String.valueOf(duty)));
        variables.addTextVariable(new TextVariable("#{ill}", String.valueOf(ill)));
        variables.addTextVariable(new TextVariable("#{hosp}", hosp));
        variables.addTextVariable(new TextVariable("#{vac}", String.valueOf(vac)));
        variables.addTextVariable(new TextVariable("#{leave}", String.valueOf(leave)));
        variables.addTextVariable(new TextVariable("#{other}", other));

    }

    private static void iteratedVariables(int iteration, Variables variables, FacultyUnit unit){
        variables.addTextVariable(new TextVariable("#{"+ iteration + "}", String.valueOf(unit.getUnitNumber())));
        variables.addTextVariable(new TextVariable("#{all" + iteration + "}", String.valueOf(unit.getGeneralCadetCount())));
        variables.addTextVariable(new TextVariable("#{pres" + iteration + "}", FacultyDataService.calculatePresentCadets(unit)));
        variables.addTextVariable(new TextVariable("#{duty" + iteration + "}", (unit.getDutyCadets().size() > 0) ? String.valueOf(unit.getDutyCadets().size()) : ""));
        variables.addTextVariable(new TextVariable("#{ill" + iteration + "}", (unit.getIllCadets().size() > 0) ? String.valueOf(unit.getIllCadets().size()) : ""));
        variables.addTextVariable(new TextVariable("#{hosp" + iteration + "}", FacultyDataService.getHospitalRepresentation(unit, "count")));
        variables.addTextVariable(new TextVariable("#{vac" + iteration + "}", (unit.getVacationsCount() > 0) ? String.valueOf(unit.getVacationsCount()) : ""));
        variables.addTextVariable(new TextVariable("#{leave" + iteration + "}", (unit.getOnLeaveCount() > 0) ? String.valueOf(unit.getOnLeaveCount()) : ""));
        variables.addTextVariable(new TextVariable("#{other" + iteration + "}", FacultyDataService.getOtherCadetInfo(unit)));

        variables.addTextVariable(new TextVariable("#{duty_names" + iteration + "}", Utils.trimList(unit.getDutyCadets())));
        variables.addTextVariable(new TextVariable("#{ill_names" + iteration + "}", Utils.trimList(unit.getIllCadets())));
        variables.addTextVariable(new TextVariable("#{hosp_names" + iteration + "}", FacultyDataService.getHospitalRepresentation(unit, "names")));
    }

}
