package org.agmip.utility.testframe;

import java.io.IOException;
import org.agmip.utility.testframe.model.TestController;
import org.agmip.utility.testframe.runner.ApsimRunner;
import org.agmip.utility.testframe.runner.ExeRunner;
import org.agmip.utility.testframe.runner.JarRunner;
import org.agmip.utility.testframe.runner.QuadUIJarRunner;

/**
 *
 * @author Meng Zhang
 */
public class App {

    public static void main(String[] args) throws IOException {
        TestController c = new TestController();
        String quaduiWorkDir = "D:\\workData\\test_data2\\Machakos_Nepal_play";
        String quaduiPath = "D:\\workData\\test_data2\\testFrame\\quadui-1.2.1-SNAPSHOT-Beta24-hf1.jar";
        String surveyFileName = "Survey_data_import_baseline.zip";
        String fieldFileName = "Field_Overlay.zip";
        String seasonalFileName = "Seasonal_strategy.zip";
        String quaduiOutputDir = "..\\testFrame\\output";
        String dssatWorkDir = "D:\\workData\\test_data2\\testFrame\\output\\DSSAT";
        String dssatExePath = "C:\\dssat45\\dscsm045.exe";
        String dssatBatchFileName = "dssbatch.v45";
        String acmouiPath = "D:\\workData\\test_data2\\testFrame\\acmoui-1.2-SNAPSHOT-beta6.jar";
        String acmoDssatInputDir = "D:\\workData\\test_data2\\testFrame\\output\\DSSAT";
        String acmoApsimInputDir = "D:\\workData\\test_data2\\testFrame\\output\\APSIM";
        String apsimWorkDir = "D:\\workData\\test_data2\\testFrame\\output\\APSIM";
        String apsimExePath = "C:\\Program Files (x86)\\Apsim75-r3008\\Model\\Apsim";
        String acmoDssatCsvFilePath = "D:\\workData\\test_data2\\testFrame\\output\\DSSAT\\ACMO-MACHAKOS-1-0XFX-0-0-DSSAT.csv";
        String acmoApsimCsvFilePath = "D:\\workData\\test_data2\\testFrame\\output\\APSIM\\ACMO-MACHAKOS-1-0XFX-0-0-APSIM.csv";

        // QuadUI
        QuadUIJarRunner quadui = new QuadUIJarRunner(quaduiPath, quaduiWorkDir, quaduiOutputDir);
        quadui.setOptions("-cli -clean -s -DAJ");
        quadui.setRawdataPath(surveyFileName);
        quadui.setLinkPath(" ");
        quadui.setOverlayPath(fieldFileName);
        quadui.setSeasonalPath(seasonalFileName);
        c.addTestRunner(quadui);
        // DSSAT
        ExeRunner dssat = new ExeRunner(dssatExePath, dssatWorkDir, "");
        dssat.setOptions("b", dssatBatchFileName);
        c.addTestRunner(dssat);
        // ACMO for DSSAT
        JarRunner acmoDssat = new JarRunner(acmouiPath);
        acmoDssat.setOptions("-cli", "-dssat", acmoDssatInputDir);
        c.addTestRunner(acmoDssat);
        // Open ACMO DSSAT CSV
        ExeRunner csvDssat = new ExeRunner("cmd");
        csvDssat.setOptions("/c", "start", "\"\"", acmoDssatCsvFilePath);
        c.addTestRunner(csvDssat);
        // APSIM
        ApsimRunner apsim = new ApsimRunner(apsimExePath, apsimWorkDir, "");
        apsim.setOptions("AgMip.apsim");
        c.addTestRunner(apsim);
        // ACMO for APSIM
        JarRunner acmoApsim = new JarRunner(acmouiPath);
        acmoApsim.setOptions("-cli", "-apsim", acmoApsimInputDir);
        c.addTestRunner(acmoApsim);
        // Open ACMO APSIM CSV
        ExeRunner csvApsim = new ExeRunner("cmd");
        csvApsim.setOptions("/c", "start", "\"\"", acmoApsimCsvFilePath);
        c.addTestRunner(csvApsim);

        c.run();
    }
}
