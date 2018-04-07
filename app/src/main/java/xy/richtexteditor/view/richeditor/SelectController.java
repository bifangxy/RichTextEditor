package xy.richtexteditor.view.richeditor;

import android.util.Log;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Xieying on 2018/4/5
 * Function:
 */
@SuppressWarnings("WeakerAccess")
public class SelectController {
    private static final String LOG_TAG = SelectController.class.getSimpleName();

    private ArrayList<Long> stateAList;

    private ArrayDeque<Long> stateBList;

    private StateTransHandler handler;

    private int num;

    public static SelectController createrController() {
        return new SelectController();
    }

    private SelectController() {
        num = 1;
        stateAList = new ArrayList<>();
        stateBList = new ArrayDeque<>(num);
    }

    public SelectController add(long id) {
        stateAList.add(id);
        return this;
    }

    public SelectController addAll(Long... ids) {
        Collections.addAll(stateAList, ids);
        return this;
    }

    public SelectController setStateBNum(int num) {
        this.num = num;
        return this;
    }

    public void changeState(long id) {
        long temp;
        if (stateAList.contains(id)) {
            stateAList.remove(id);
            if (num > 0 && stateBList.size() >= num) {
                temp = stateBList.poll();
                stateAList.add(temp);
                if (handler != null)
                    handler.handleB2A(temp);
            }
            stateBList.add(id);
            if (handler != null) {
                handler.handleA2B(id);
            }
        } else if (stateBList.contains(id)) {
            stateBList.remove(id);
            stateAList.add(id);
            if (handler != null)
                handler.handleB2A(id);
        }
    }

    public void reset() {
        moveAll2StateA();
    }

    private void moveAll2StateA() {
        while (!stateBList.isEmpty()) {
            long temp = stateBList.poll();
            stateAList.add(temp);
            handler.handleB2A(temp);
        }
    }

    public boolean contain(long id) {
        return stateAList.contains(id) || stateBList.contains(id);
    }

    public void setHandler(StateTransHandler handler) {
        this.handler = handler;
    }

    public interface StateTransHandler {
        void handleA2B(long id);

        void handleB2A(long id);
    }

    @SuppressWarnings("unused")
    public abstract class StateTransAdapter implements StateTransHandler {
        @Override
        public void handleA2B(long id) {
            Log.d(LOG_TAG, "handleA2B" + id);
        }

        @Override
        public void handleB2A(long id) {
            Log.d(LOG_TAG, "handleB2A" + id);
        }
    }


}
