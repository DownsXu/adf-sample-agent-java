package DX_team.extaction.police;

import adf.core.agent.info.AgentInfo;
import rescuecore.objects.Road;
import rescuecore2.misc.geometry.Point2D;
import rescuecore2.misc.geometry.Vector2D;

/**
 * 道路引导
 */
public class RoadGuidance {

    /**
     * 警察智能体在的位置
     */
    private Point2D position;

    /**
     * 第一条道路向量引导线
     */
    private Vector2D line1;

    /**
     * 第二条道路向量引导线
     */
    private Vector2D line2;

    /**
     * 警察所在位置
     */
    private Road selfRoad;

    /**
     * 警察即将移动的下一条道路
     */
    private Road neighborRoad;
    private AgentInfo agentInfo;

    public RoadGuidance(Point2D position) {
        this.position = position;
    }

    public Vector2D getLine1() {
        return line1;
    }

    public void setLine1(Vector2D line1) {
        this.line1 = line1;
    }

    public Vector2D getLine2() {
        return line2;
    }

    public void setLine2(Vector2D line2) {
        this.line2 = line2;
    }

    public void calc(){
        Point2D policePosition = this.position;

    }
}
