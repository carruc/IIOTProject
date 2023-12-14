package model.descriptors;

/** Model for a video recognition system monitoring
 * the number of people in a room.
 */

public class VideocameraDescriptor extends GenericDescriptor<Integer>{

    public VideocameraDescriptor(Integer value) {
        super(value);
    }

    @Override
    public void refreshValue() {

    }
}
