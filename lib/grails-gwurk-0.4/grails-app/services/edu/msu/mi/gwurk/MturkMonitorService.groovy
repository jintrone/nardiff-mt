package edu.msu.mi.gwurk

import com.amazonaws.AmazonServiceException
import com.amazonaws.services.mturk.model.AmazonMTurkException
import com.amazonaws.services.mturk.model.ServiceException
import grails.transaction.Transactional
import groovy.util.logging.Log4j
import org.apache.shiro.crypto.hash.Sha256Hash

@Log4j
@Transactional
class MturkMonitorService {

    Timer heartBeat;
    boolean running
    long pauseTime = 20000l
    def listeners = [] as Set<BeatListener>

    def init() {
        restart()
        def user = new ShiroUser(username: "administrator", passwordHash: new Sha256Hash("mturk123").toHex())
        user.addToPermissions("*:*")
        user.save()
    }

    def halt() {
        if (running) {
            heartBeat.cancel()
        }
    }



    public void cleanup() {
        heartBeat = null
        log.info("Would be cleaning up")
    }

    def restart() {
        if (running) {
            log.warn("Timer is already running; please use halt to stop if you wish to restart");
            return;

        }

        heartBeat = new Timer() {
            public void cancel() {
                super.cancel();
                running = false;
                cleanup();
            }
        }

        heartBeat.schedule(new TimerTask() {

            public boolean cancel() {
                boolean result = super.cancel();
                heartBeat.cancel();
                return result;

            }

            @Override
            public void run() {
                running = true;
                try {
                    beat();


                } catch (ServiceException e1) {
                    log.warn("AWS Service Exception: ");
                    e1.printStackTrace();
                    log.warn("Continuing");

                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    // exceptions are far too numerous and varied to give up after just one!
                    //heartBeat.cancel();
                }


            }
        }, 0, pauseTime);
    }

    def launch(Workflow w, boolean real, int iterations, Credentials credentials,Map props) {
        log.info("Attempting to launch workflow")
        WorkflowRun run = new WorkflowRun(w,credentials,real,props)
        run.save(flush:true,failOnError: true)



    }

    def actualLaunch(WorkflowRun run,int iterations) {
        run.run(iterations)
        listeners.add(run)
        log.info("Done launching workflow: "+run)
    }

    def safeBeat(Object beater, Collection<BeatListener> llist, Closure c = null) throws AmazonServiceException {

        List<BeatListener> tmp =  []+llist
        long sleep = 500
        while (!tmp.isEmpty()) {


            log.info("Running ${tmp.size()}")
            BeatListener f = tmp.first()
            if (f instanceof WorkflowRun) {
                f = WorkflowRun.get(f.id)
                if (f == null) {
                    log.warn("Could not retrieve workflow")
                    tmp.remove(0)
                }
            }
            if (f!=null)  {
                try {
                    f.beat(beater, System.currentTimeMillis())
                    if (c!=null) {
                        c(f)
                    }
                    sleep = 500
                    tmp.remove(0)
                } catch (AmazonMTurkException ex) {
                    if (ex.statusCode == 400) {
                        log.warn("Sleeping due to throttling")
                        Thread.sleep(sleep)
                        sleep*=2
                    } else {
                        throw ex
                    }
                }
            }

        }

    }

    def beat() throws AmazonServiceException {
        log.info "Dropping the beat"
        safeBeat(this,listeners)
        listeners.removeAll {
            it.hasProperty("currentStatus") && it.currentStatus == WorkflowRun.Status.DONE

        }
    }
}
