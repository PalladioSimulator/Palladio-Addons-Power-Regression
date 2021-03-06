package de.fzi.power.regression.r;

import java.util.List;
import java.util.Vector;

import javax.measure.quantity.Quantity;

import org.rosuda.REngine.REXP;

import de.fzi.power.regression.r.io.RRegressionConnection;
import de.fzi.power.regression.r.io.RRegressionConnectionImpl;
import de.fzi.power.regression.r.io.RUtils;

public abstract class AbstractRegression<Q extends Quantity> {

    protected static final String FIT_FNC = "fitFnc";
    protected static final String DATA_FRAME_NAME = "df";
    protected static final String R_REGRESSION_RELATIONHSIP_OPERATOR = "~";
    protected static final String R_PARAM_SEPARATOR = ", ";
    protected static final String R_START_PARAM_NAME_ASSIGN = "start=";
    protected static final String R_START_VALUES_BLOCK = "list(";
    protected static final String R_START_VALUE_ASSIGNMENT_OPERATOR = "=";
    protected static final String R_BLOCK_END = ")";
    protected static final String R_ADDITIONAL_COMMAND = "control=c(maxiter=5000, minFactor=1/4096)";
    protected static final String R_COMMAND_POSTFIX = ");";
    protected static final String R_TARGET_NAME = "targetValue";
    protected static final String R_ASSIGNMENT_OPERATOR = " <- ";
    protected static final String R_ACCESSOR = "$";
    
    protected TargetMeasurements targetMetric;
    protected List<VariableMeasurements> measurements;
    

    public AbstractRegression(TargetMeasurements targetMetric, List<VariableMeasurements> measurements) {
        this.targetMetric = targetMetric;
        this.measurements = measurements;
    }

    protected void constructModel() {
        RRegressionConnection rConnection = RRegressionConnectionImpl.getRRegressionConnection();
        RUtils.ensurePackageAvailability(getRequiredPackages(), rConnection);
        
        String formula = getFormula();
        
        String targetName = RUtils.sanitizeNameForR(targetMetric.getName());
        rConnection.assign(targetName, targetMetric.getValues());
        
        StringBuilder dataFrameBuilder = new StringBuilder(DATA_FRAME_NAME + R_ASSIGNMENT_OPERATOR + "data.frame(");
        
        for(Measurements variableMeasurements : measurements) {
            rConnection.assign(variableMeasurements.getName(), variableMeasurements.getValues());
            dataFrameBuilder.append(variableMeasurements.getName() + R_PARAM_SEPARATOR);
        }
        
        dataFrameBuilder.append(targetName);
        
        dataFrameBuilder.append(R_BLOCK_END);
        
        String dataFrameCommand = dataFrameBuilder.toString();
        rConnection.execute(dataFrameCommand);

        
        StringBuilder commandString = constructRegularRegressionExpression(formula);
        String command = commandString.toString();
        
        // Use the same random seed for all executions.
        rConnection.execute("set.seed(47)");
        
        Vector<REXP> rawResults = rConnection.execute(R_TARGET_NAME + " " + R_ASSIGNMENT_OPERATOR + command);
    }

    private StringBuilder constructRegularRegressionExpression(String formula) {
        StringBuilder commandString = new StringBuilder(getFunctionName());
        commandString.append(RUtils.sanitizeNameForR(targetMetric.getName()));
        commandString.append(R_REGRESSION_RELATIONHSIP_OPERATOR);
        // TODO: check if parentheses needed due to issue with linear expressions that may manifest otherwise
        commandString.append(formula);
        commandString.append(R_PARAM_SEPARATOR);
        commandString.append("data = " + DATA_FRAME_NAME);
        String params = getAdditionalParameters();
        if(params != null && !params.equals("")) {
            commandString.append(R_PARAM_SEPARATOR + params);
        }
        commandString.append(R_COMMAND_POSTFIX);
        return commandString;
    }

    public abstract String getFormula();

    public abstract Iterable<String> getRequiredPackages();
    
    public abstract String getFunctionName();
    protected abstract String getAdditionalParameters();
    
}