package edu.vuum.mocca;

import android.util.SparseArray;

/**
 * @class ButtonStrategyMapper
 *
 * @brief Maps buttons (represented via their resource ids) to
 *        ButtonStrategy implementations.
 */
public class ButtonStrategyMapper {
    /**
     * Data structure that maps button Ids to ButtonStrategy objects.
     */
    private SparseArray<ButtonStrategy> mButtonStrategyArray =
        new SparseArray<>();
            
    /**
     * Constructor that uses SparseArray maps button Ids to
     * ButtonStrategy objects.
     */ 
    public ButtonStrategyMapper(int[] buttonIds,
                                ButtonStrategy[] buttonStrategys) {
        // Map buttons pushed by the user to the requested type of
        // ButtonStrategy.
        for (int i = 0; i < buttonIds.length; ++i)
            mButtonStrategyArray.put(buttonIds[i],
                                     buttonStrategys[i]);
    }

    /**
     * Factory method that returns the request ButtonStrategy
     * implementation given a button Id.
     */
    public ButtonStrategy getButtonStrategy(int buttonId) {
        // Return the designated ButtonStrategy.
        return mButtonStrategyArray.get(buttonId);
    }
}

