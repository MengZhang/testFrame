package org.agmip.utility.testframe.runner;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Meng Zhang
 */
public class QuadUIJarRunner extends JarRunner {

//    private static Logger LOG = LoggerFactory.getLogger(QuadUIJarRunner.class);
    private File rawdataPath = new File(" ");
    private File linkPath = new File(" ");
    private File overlayPath = new File(" ");
    private File seasonalPath = new File(" ");
//    private boolean isZipFlg = false;
//    private boolean isCleanFlg = false;
//    private String mode = "none";
//    private String outputModels = "json";

    public QuadUIJarRunner(String executablePath, String workDir, String outputDir) {
        super(executablePath, workDir, outputDir);
    }

    public QuadUIJarRunner(String executablePath) {
        this(executablePath, "", "");
    }

    public void setRawdataPath(String rawdataPath) {
        this.rawdataPath = new File(rawdataPath);
    }

    public void setLinkPath(String linkPath) {
        this.linkPath = new File(linkPath);
    }

    public void setOverlayPath(String overlayPath) {
        this.overlayPath = new File(overlayPath);
    }

    public void setSeasonalPath(String seasonalPath) {
        this.seasonalPath = new File(seasonalPath);
    }

//    public void setMode(String mode) {
//        if (mode == null || mode.equals("")) {
//            LOG.warn("Invalid mode value {} for QuadUI!", mode);
//        } else if (mode.contains("raw") || mode.contains("none")) {
//            this.mode = "none";
//        } else if (mode.contains("field") || mode.contains("overlay")) {
//            this.mode = "field";
//        } else if (mode.contains("strategy") || mode.contains("seasonal")) {
//            this.mode = "strategy";
//        } else {
//            LOG.warn("Invalid mode value {} for QuadUI!", mode);
//        }
//    }
    @Override
    public ArrayList<String> getProcessArguments() {
        ArrayList<String> argsList = new ArrayList();
        argsList.addAll(super.getProcessArguments());
        argsList.remove(argsList.size() - 1);
        argsList.add(rawdataPath.getPath());
        argsList.add(linkPath.getPath());
        argsList.add(overlayPath.getPath());
        argsList.add(seasonalPath.getPath());
        argsList.add(outputDir.getPath());
        return argsList;
    }
}
