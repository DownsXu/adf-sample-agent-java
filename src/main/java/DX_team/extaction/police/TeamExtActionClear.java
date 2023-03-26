package DX_team.extaction.police;

import adf.core.agent.develop.DevelopData;
import adf.core.agent.info.AgentInfo;
import adf.core.agent.info.ScenarioInfo;
import adf.core.agent.info.WorldInfo;
import adf.core.agent.module.ModuleManager;
import adf.core.component.extaction.ExtAction;
import adf.core.component.module.algorithm.PathPlanning;
import rescuecore2.standard.entities.*;
import rescuecore2.worldmodel.EntityID;

/**
 * @BelongsProject: adf-sample-agent-java
 * @BelongsPackage: DX_team.extaction
 * @Author: DownsXu
 * @Date: 2023-03-19 20:10
 * @Description: TODO
 */


/**
 * 清理动作
 */
public class TeamExtActionClear extends ExtAction {

    /**
     * 警察的动作目标
     */
    private EntityID target;


    /**
     * 路径规划算法
     */
    private final PathPlanning pathPlanning;


    public TeamExtActionClear(AgentInfo ai, WorldInfo wi, ScenarioInfo si, ModuleManager moduleManager, DevelopData developData, PathPlanning pathPlanning) {
        super(ai, wi, si, moduleManager, developData);
        this.pathPlanning = moduleManager.getModule("DefaultExtActionClear.PathPlanning", "adf.impl.module.algorithm.DijkstraPathPlanning");

    }

    /**
     * 设置警察目标target
     * @param target
     * @return this
     */
    @Override
    public ExtAction setTarget(EntityID target) {
        this.target = null;

        StandardEntity entity = this.worldInfo.getEntity(target);
        if (entity != null){
            if (entity instanceof Road){
                this.target = target;
            } else if (entity.getStandardURN().equals(StandardEntityURN.BLOCKADE)){
                this.target = ((Blockade)entity).getPosition();
            } else if (entity instanceof Building){
                this.target = target;
            }
        }
        return this;
    }

    @Override
    public ExtAction calc() {
        return null;
    }

//    @Override
//    public ExtAction calc() {
//        this.result = null;
//        PoliceForce policeForce = (PoliceForce) this.agentInfo.me();
//
//        if (this.needRest(policeForce)) {
//            List<EntityID> list = new ArrayList<>();
//            if (this.target != null) {
//                list.add(this.target);
//            }
//            this.result = this.calcRest(policeForce, this.pathPlanning, list);
//            if (this.result != null) {
//                return this;
//            }
//        }
//
//        if (this.target == null) {
//            return this;
//        }
//
//
//    }
//
//    private Action calcRest(PoliceForce policeForce, PathPlanning pathPlanning, List<EntityID> list) {
//
//    }
//
//    private boolean needRest(PoliceForce policeForce) {
//
//    }


}


