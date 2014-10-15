package org.agmip.utility.testframe.comparator;

import au.com.bytecode.opencsv.CSVReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Meng Zhang
 */
public class AcmoCsvFileComparator extends CsvFileComparator {

    private static final Logger LOG = LoggerFactory.getLogger(AcmoCsvFileComparator.class);

    protected AcmoCsvFileComparator(File expected, File actual) throws Exception {
        super(expected, actual);
        if (!expected.getName().toUpperCase().startsWith("ACMO-")) {
            throw new Exception("Expected ACMO CSV file is invalid [" + expected.getName() + "]");
        } else if (!expected.getName().toUpperCase().startsWith("ACMO-")) {
            throw new Exception("Actual ACMO CSV file is invalid [" + actual.getName() + "]");
        }
        this.keyHeaderName = "EXNAME";
        this.headerRawNum = 2;        
    }
    
    public void setCompareAllOutputCols() {
        this.compareHeaderNames = new ArrayList(Arrays.asList("HWAH_S", "CWAH_S", "ADAT_S", "MDAT_S", "HADAT_S", "LAIX_S", "PRCP_S", "ETCP_S", "NUCM_S", "NLCM_S", "EPCP_S", "ESCP_S"));
    }
}
