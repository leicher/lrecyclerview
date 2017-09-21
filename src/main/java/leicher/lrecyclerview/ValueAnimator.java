package leicher.lrecyclerview;

class ValueAnimator extends android.animation.ValueAnimator {

    private int target;



    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getValue(){
        return (int) getAnimatedValue();
    }

    static abstract class AnimatorUpdateListener implements android.animation.ValueAnimator.AnimatorUpdateListener{


        @Override
        public void onAnimationUpdate(android.animation.ValueAnimator animation) {
            onAnimationUpdate((ValueAnimator) animation);
        }

        public abstract void onAnimationUpdate(ValueAnimator animation);

    }
}
