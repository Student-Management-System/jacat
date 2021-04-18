package net.ssehub.jacat.addon.paresultprocessor;

import net.ssehub.jacat.api.AbstractJacatWorker;
import net.ssehub.jacat.api.addon.Addon;
import net.ssehub.jacat.api.studmgmt.IStudMgmtClient;
import net.ssehub.jacat.api.studmgmt.IStudMgmtFacade;

public class Main extends Addon {

    @Override
    public void onEnable() {
        AbstractJacatWorker worker = this.getWorker();
        IStudMgmtFacade studMgmtFacade = worker.getStudMgmtFacade();
        worker.registerResultProcessor(this, new SimilaritiesResultProcessor(studMgmtFacade));
    }
}
