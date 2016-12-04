package adf.sample.tactics;

import adf.agent.action.Action;
import adf.agent.action.common.ActionMove;
import adf.agent.action.common.ActionRest;
import adf.agent.action.police.ActionClear;
import adf.agent.communication.MessageManager;
import adf.agent.communication.standard.bundle.centralized.CommandPolice;
import adf.agent.communication.standard.bundle.centralized.CommandScout;
import adf.agent.communication.standard.bundle.centralized.MessageCommand;
import adf.agent.communication.standard.bundle.information.MessagePoliceForce;
import adf.agent.develop.DevelopData;
import adf.agent.info.AgentInfo;
import adf.agent.info.ScenarioInfo;
import adf.agent.info.WorldInfo;
import adf.agent.module.ModuleManager;
import adf.agent.precompute.PrecomputeData;
import adf.component.communication.CommunicationMessage;
import adf.component.extaction.CommandExecutor;
import adf.component.extaction.ExtAction;
import adf.component.module.complex.RoadDetector;
import adf.component.module.complex.Search;
import adf.component.tactics.TacticsPoliceForce;
import rescuecore2.standard.entities.*;
import rescuecore2.worldmodel.EntityID;

import java.util.List;

public class SampleTacticsPoliceForce extends TacticsPoliceForce {
    private int clearDistance;

    private RoadDetector roadDetector;
    private Search search;

    private ExtAction actionExtClear;
    private ExtAction actionExtMove;
    private CommandExecutor actionCommandPolice;

    @Override
    public void initialize(AgentInfo agentInfo, WorldInfo worldInfo, ScenarioInfo scenarioInfo, ModuleManager moduleManager, MessageManager messageManager, DevelopData developData) {
        worldInfo.indexClass(
                StandardEntityURN.ROAD,
                StandardEntityURN.HYDRANT,
                StandardEntityURN.BUILDING,
                StandardEntityURN.REFUGE,
                StandardEntityURN.BLOCKADE
        );
        // init value
        this.clearDistance = scenarioInfo.getClearRepairDistance();
        // init Algorithm Module & ExtAction
        switch  (scenarioInfo.getMode()) {
            case PRECOMPUTATION_PHASE:
                this.search = moduleManager.getModule("TacticsPoliceForce.Search", "adf.sample.module.complex.SampleSearch");
                this.roadDetector = moduleManager.getModule("TacticsPoliceForce.RoadDetector", "adf.sample.module.complex.SampleRoadDetector");
                this.actionExtClear = moduleManager.getExtAction("TacticsPoliceForce.ActionExtClear", "adf.sample.extaction.ActionExtClear");
                this.actionExtMove = moduleManager.getExtAction("TacticsPoliceForce.ActionExtMove", "adf.sample.extaction.ActionExtMove");
                this.actionCommandPolice = moduleManager.getCommandExecutor("TacticsPoliceForce.CommandExecutorPolice", "adf.sample.extaction.CommandExecutorPolice");
                break;
            case PRECOMPUTED:
                this.search = moduleManager.getModule("TacticsPoliceForce.Search", "adf.sample.module.complex.SampleSearch");
                this.roadDetector = moduleManager.getModule("TacticsPoliceForce.RoadDetector", "adf.sample.module.complex.SampleRoadDetector");
                this.actionExtClear = moduleManager.getExtAction("TacticsPoliceForce.ActionExtClear", "adf.sample.extaction.ActionExtClear");
                this.actionExtMove = moduleManager.getExtAction("TacticsPoliceForce.ActionExtMove", "adf.sample.extaction.ActionExtMove");
                this.actionCommandPolice = moduleManager.getCommandExecutor("TacticsPoliceForce.CommandExecutorPolice", "adf.sample.extaction.CommandExecutorPolice");
                break;
            case NON_PRECOMPUTE:
                this.search = moduleManager.getModule("TacticsPoliceForce.Search", "adf.sample.module.complex.SampleSearch");
                this.roadDetector = moduleManager.getModule("TacticsPoliceForce.RoadDetector", "adf.sample.module.complex.SampleRoadDetector");
                this.actionExtClear = moduleManager.getExtAction("TacticsPoliceForce.ActionExtClear", "adf.sample.extaction.ActionExtClear");
                this.actionExtMove = moduleManager.getExtAction("TacticsPoliceForce.ActionExtMove", "adf.sample.extaction.ActionExtMove");
                this.actionCommandPolice = moduleManager.getCommandExecutor("TacticsPoliceForce.CommandExecutorPolice", "adf.sample.extaction.CommandExecutorPolice");
                break;
        }
    }

    @Override
    public void precompute(AgentInfo agentInfo, WorldInfo worldInfo, ScenarioInfo scenarioInfo, ModuleManager moduleManager, PrecomputeData precomputeData, DevelopData developData) {
        this.search.precompute(precomputeData);
        this.roadDetector.precompute(precomputeData);
        this.actionExtClear.precompute(precomputeData);
        this.actionExtMove.precompute(precomputeData);
        this.actionCommandPolice.precompute(precomputeData);
    }

    @Override
    public void resume(AgentInfo agentInfo, WorldInfo worldInfo, ScenarioInfo scenarioInfo, ModuleManager moduleManager, PrecomputeData precomputeData, DevelopData developData) {
        this.search.resume(precomputeData);
        this.roadDetector.resume(precomputeData);
        this.actionExtClear.resume(precomputeData);
        this.actionExtMove.resume(precomputeData);
        this.actionCommandPolice.resume(precomputeData);
    }

    @Override
    public void preparate(AgentInfo agentInfo, WorldInfo worldInfo, ScenarioInfo scenarioInfo, ModuleManager moduleManager, DevelopData developData) {
        this.search.preparate();
        this.roadDetector.preparate();
        this.actionExtClear.preparate();
        this.actionExtMove.preparate();
        this.actionCommandPolice.preparate();
    }

    @Override
    public Action think(AgentInfo agentInfo, WorldInfo worldInfo, ScenarioInfo scenarioInfo, ModuleManager moduleManager, MessageManager messageManager, DevelopData developData) {
        this.search.updateInfo(messageManager);
        this.roadDetector.updateInfo(messageManager);
        this.actionExtClear.updateInfo(messageManager);
        this.actionExtMove.updateInfo(messageManager);
        this.actionCommandPolice.updateInfo(messageManager);

        PoliceForce agent = (PoliceForce) agentInfo.me();
        // command
        MessageCommand command = this.getCommand(agentInfo, messageManager);
        if(command != null) {
            Action action = this.actionCommandPolice.setCommand(command).calc().getAction();
            if (action != null) {
                this.sendActionMessage(worldInfo, messageManager, agent, action);
                return action;
            }
        }
        // autonomous
        EntityID target = this.roadDetector.calc().getTarget();
        Action action = this.actionExtClear.setTarget(target).calc().getAction();
        if(action != null) {
            this.sendActionMessage(worldInfo, messageManager, agent, action);
            return action;
        }

        target = this.search.calc().getTarget();
        action = this.actionExtMove.setTarget(target).calc().getAction();
        if(action != null) {
            this.sendActionMessage(worldInfo, messageManager, agent, action);
            return action;
        }

        messageManager.addMessage(
                new MessagePoliceForce(true, agent, MessagePoliceForce.ACTION_REST, agent.getPosition())
        );
        return new ActionRest();
    }

    private void sendActionMessage(WorldInfo worldInfo, MessageManager messageManager, PoliceForce policeForce, Action action) {
        Class<? extends Action> actionClass = action.getClass();
        int actionIndex = -1;
        EntityID target = null;
        if(actionClass == ActionMove.class) {
            List<EntityID> path = ((ActionMove)action).getPath();
            actionIndex = MessagePoliceForce.ACTION_MOVE;
            if(path.size() > 0) {
                target = path.get(path.size() - 1);
            }
        } else if(actionClass == ActionClear.class) {
            actionIndex = MessagePoliceForce.ACTION_CLEAR;
            ActionClear ac = (ActionClear)action;
            target = ac.getTarget();
            if(target == null) {
                for(StandardEntity entity : worldInfo.getObjectsInRange(ac.getPosX(), ac.getPosY(), this.clearDistance)) {
                    if(entity.getStandardURN() == StandardEntityURN.BLOCKADE) {
                        target = entity.getID();
                        break;
                    }
                }
            }
        } else if(actionClass == ActionRest.class) {
            actionIndex = MessagePoliceForce.ACTION_REST;
            target = policeForce.getPosition();
        }
        if(actionIndex != -1) {
            messageManager.addMessage(new MessagePoliceForce(true, policeForce, actionIndex, target));
        }
    }

    private MessageCommand getCommand(AgentInfo agentInfo, MessageManager messageManager) {
        MessageCommand result = null;
        EntityID agentID = agentInfo.getID();
        for(CommunicationMessage message : messageManager.getReceivedMessageList(CommandScout.class)) {
            CommandScout command = (CommandScout) message;
            if(command.isToIDDefined() && command.getToID().getValue() == agentID.getValue()) {
                result = command;
                break;
            }
        }
        for(CommunicationMessage message : messageManager.getReceivedMessageList(CommandPolice.class)) {
            CommandPolice command = (CommandPolice) message;
            if(command.isToIDDefined() && command.getToID().getValue() == agentID.getValue()) {
                result = command;
                break;
            }
        }
        return result;
    }
}