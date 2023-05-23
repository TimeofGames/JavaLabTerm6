public class CalculationThread extends Thread {
    private double min;
    private double max;
    private double step;
    private final ResultResource result;

    public CalculationThread(double min, double max, double step, ResultResource result) {
        this.min = min;
        this.max = max;
        this.step = step;
        this.result = result;
    }

    public void run() {
        double localResult = 0;
        for (double i = min; i < max - step; i += step) {
            if (i > max) {
                localResult += (Math.cos(i - step) + Math.cos(max)) / 2 * step;
            } else {
                localResult += (Math.cos(i) + Math.cos(i + step)) / 2 * step;
            }
        }

        synchronized (result) {
            result.getModel().setValueAt(
                     Double.parseDouble(String.valueOf(result.getModel().getDataVector().get(result.getSelectedRow()).get(3))) + localResult,
                    result.getSelectedRow(), 3);
            result.getData().get(result.getSelectedRow()).setDataByIndex(3,
                    result.getData().get(result.getSelectedRow()).getData().get(3) + localResult);
        }

    }
}
