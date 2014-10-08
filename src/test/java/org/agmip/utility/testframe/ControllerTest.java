package org.agmip.utility.testframe;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.junit.Ignore;
import org.junit.Test;
import org.agmip.utility.testframe.model.TestController;
import org.agmip.utility.testframe.runner.ApsimRunner;
import org.agmip.utility.testframe.runner.ExeRunner;
import org.agmip.utility.testframe.runner.JarRunner;
import org.agmip.utility.testframe.runner.QuadUIJarRunner;
import org.junit.Before;

/**
 * Unit test for Test Controller to run a test flow.
 *
 * @author Meng Zhang
 */
public class ControllerTest {

    URL testFlowDef;

    /**
     * setup the test data
     */
    @Before
    public void setUp() throws Exception {
        testFlowDef = this.getClass().getResource("/testFlow.json");
    }

    /**
     * test the functionality from test flow definition in a JSON file
     *
     * @throws IOException, Exception
     */
    @Test
    @Ignore
    public void testViaJsonDefinition() throws IOException, Exception {
        TestController c = new TestController();
        c.readDefJson(new File(testFlowDef.getPath()));
        c.run();
    }

    /**
     * test the functionality from test flow definition in a JSON file
     *
     * @throws java.io.IOException
     */
    @Test
    @Ignore
    public void testViaHardCode() throws IOException, Exception {
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
        quadui.setArguments("-cli -clean -s -DAJ");
        quadui.setRawdataPath(surveyFileName);
        quadui.setLinkPath(" ");
        quadui.setOverlayPath(fieldFileName);
        quadui.setSeasonalPath(seasonalFileName);
        c.addAppRunner(quadui);
        // DSSAT
        ExeRunner dssat = new ExeRunner(dssatExePath, dssatWorkDir, "");
        dssat.setArguments("b", dssatBatchFileName);
        c.addAppRunner(dssat);
        // ACMO for DSSAT
        JarRunner acmoDssat = new JarRunner(acmouiPath);
        acmoDssat.setArguments("-cli", "-dssat", acmoDssatInputDir);
        c.addAppRunner(acmoDssat);
        // Open ACMO DSSAT CSV
        ExeRunner csvDssat = new ExeRunner("cmd");
        csvDssat.setArguments("/c", "start", "\"\"", acmoDssatCsvFilePath);
        c.addAppRunner(csvDssat);
        // APSIM
        ApsimRunner apsim = new ApsimRunner(apsimExePath, apsimWorkDir, "");
        apsim.setArguments("AgMip.apsim");
        c.addAppRunner(apsim);
        // ACMO for APSIM
        JarRunner acmoApsim = new JarRunner(acmouiPath);
        acmoApsim.setArguments("-cli", "-apsim", acmoApsimInputDir);
        c.addAppRunner(acmoApsim);
        // Open ACMO APSIM CSV
        ExeRunner csvApsim = new ExeRunner("cmd");
        csvApsim.setArguments("/c", "start", "\"\"", acmoApsimCsvFilePath);
        c.addAppRunner(csvApsim);

        c.run();
    }
}
