package net.ssehub.jacat.addon.paresultprocessor;

import net.ssehub.jacat.api.AbstractJacatWorker;
import net.ssehub.jacat.api.addon.Addon;
import net.ssehub.jacat.api.studmgmt.IStudMgmtClient;

public class Main extends Addon {

    @Override
    public void onEnable() {
        AbstractJacatWorker worker = this.getWorker();
        IStudMgmtClient studMgmtClient = this.getWorker().getStudMgmtClient();
        worker.registerResultProcessor(this, new SimilaritiesResultProcessor(studMgmtClient));
    }
}
