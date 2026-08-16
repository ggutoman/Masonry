package com.gag.accounting.ViewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.gag.appdriver.App.Core.Projects;
import org.gag.appdriver.App.Models.LodgeCalendarList;
import org.gag.appdriver.App.Models.MemberPledges;
import org.gag.appdriver.App.Models.ProjectDetail;
import org.gag.appdriver.Constants.PROJECT_CONSTANTS;
import org.gag.appdriver.Room.Entities.EMemberInfo;
import org.gag.appdriver.Room.Entities.EProjectDetail;
import org.gag.appdriver.Room.Entities.EProjectMaster;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class VM_Projects extends AndroidViewModel {

    private final Projects poProjects;
    private final MutableLiveData<List<ProjectDetail>> GetProjectDetail = new MutableLiveData<>();

    public interface OnTransaction{
        void OnLoad();
        void OnSuccess();
        void OnFailed(String fsMessage);
    }

    public interface OnDownload{
        void OnLoad();
        void OnFinished(String fsMessage);
    }

    public VM_Projects(@NonNull Application application) {
        super(application);

        poProjects = new Projects(application);
    }

    public String GetUserID(){
        return poProjects.GetUserID();
    }

    public String GetCurrentDate(){
        return poProjects.GetCurrentDate();
    }

    public String GetFormattedDate(String fsDate, String fsFormat){
        return poProjects.FormatDateString(fsDate, fsFormat);
    }

    public String GetFormatLongDate(long fsDate){
        return poProjects.FormatLongDate(fsDate);
    }

    public String GetCurrentDateTime(){
        return poProjects.GetCurrentDateTime();
    }

    public List<PROJECT_CONSTANTS> GetProjectTypes(){
        return new ArrayList<>(List.of(PROJECT_CONSTANTS.EDUCATION, PROJECT_CONSTANTS.HEALTH, PROJECT_CONSTANTS.OUTREACH, PROJECT_CONSTANTS.ENVIRONMENTAL));
    }

    public List<PROJECT_CONSTANTS> GetProjectStatus(){
        return new ArrayList<>(List.of(PROJECT_CONSTANTS.PLANNING, PROJECT_CONSTANTS.ON_GOING, PROJECT_CONSTANTS.COMPLETED, PROJECT_CONSTANTS.CANCELLED));
    }

    public LiveData<List<LodgeCalendarList>> GetLodgeCalendarList(){
        return poProjects.ObserveLodgeCalendarList();
    }

    public LiveData<LodgeCalendarList> GetLodgeCalendarInfo(String fsYearIDxx){
        return poProjects.GetLodgeCalendarInfo(fsYearIDxx);
    }

    public LiveData<EProjectMaster> GetProject(String fsProjectCd){
        return poProjects.GetProject(fsProjectCd);
    }

    public LiveData<List<ProjectDetail>> GetProjectDetails(String fsProjectCd){
        return poProjects.GetProjectDetails(fsProjectCd);
    }

    public LiveData<List<ProjectDetail>> GetPledgeSummary(String fsProjectCd, String fsDfrom, String fsDto){
        return poProjects.GetPledgeSummary(fsProjectCd, fsDfrom, fsDto);
    }

    public LiveData<List<ProjectDetail>> GetProjectList(){
        return GetProjectDetail;
    }

    public LiveData<List<EMemberInfo>> GetMemberList(){
        return poProjects.ObserveMemberList();
    }

    public LiveData<List<EProjectMaster>> GetProjectList(String fsYearIDxx, String fsDfrom, String fsDto){
        return poProjects.GetProjectList(fsYearIDxx, fsDfrom, fsDto);
    }

    public LiveData<List<MemberPledges>> GetMemberPledges(String fsMemberIDxx, String fsDfrom, String fsDto){
        return poProjects.GetMemberPledges(fsMemberIDxx, fsDfrom, fsDto);
    }

    public boolean AddProjectDetail(String fsTransNox, String fsMemberID, String fsMemberNme, String fdPledge, String fnPledge){

        ProjectDetail loProject = new ProjectDetail(
                fsTransNox,
                fsMemberID,
                fsMemberNme,
                fdPledge,
                fnPledge,
                "",
                "0.00"
        );

        List<ProjectDetail> currentList = GetProjectDetail.getValue();
        if (currentList == null) {
            currentList = new ArrayList<>();
        }

        boolean exists = false;
        for (ProjectDetail detail : currentList) {

            if (detail.getSMemberID().equalsIgnoreCase(fsMemberID) &&
                    detail.getDPledgexx().equalsIgnoreCase(fdPledge)) {

                exists = true;
                break;
            }
        }
        if (exists) {
            return false;
        }
        currentList.add(loProject);

        GetProjectDetail.setValue(currentList);

        return true;
    }

    public boolean ReplaceProjectDetail(int fnIndex, String fsMemberID, String fdPledge, String fnPledge){

        List<ProjectDetail> currentList = GetProjectDetail.getValue();
        if (currentList == null) return false;

        boolean hasSamePledge = false;
        for (int i = 0; i < currentList.size(); i++){

            if (currentList.get(i).getSMemberID().equalsIgnoreCase(fsMemberID) && i != fnIndex) {

                if (currentList.get(i).getDPledgexx().equalsIgnoreCase(fdPledge)){
                    hasSamePledge = true;
                    break;
                }
            }
        }

        //do not replace if the same date of pledge of same member entry
        if (hasSamePledge){
            return false;
        }

        ProjectDetail loItem = new ProjectDetail(
                currentList.get(fnIndex).getSProjectCd(),
                currentList.get(fnIndex).getSMemberID(),
                currentList.get(fnIndex).getSMemberNme(),
                fdPledge,
                fnPledge,
                "",
                "0.00"
        );
        currentList.set(fnIndex, loItem);

        GetProjectDetail.setValue(currentList);
        return true;
    }

    public void ClearDetail(){
        GetProjectDetail.setValue(new ArrayList<>());
    }

    public void SaveProject(EProjectMaster foMaster, List<ProjectDetail> foDetail, String fcStatus, OnTransaction foCallback){

        foCallback.OnLoad();

        //check required prppertoes
        if (foMaster.getSYearIDxx().isEmpty()){
            foCallback.OnFailed("Year id is not initialized");
            return;
        }
        if (foMaster.getDTransact().isEmpty()){
            foCallback.OnFailed("Transaction date is not initialized");
            return;
        }else if (foMaster.getCTranStat().isEmpty()){
            foCallback.OnFailed("Project status is not initialized");
            return;
        }else if (foMaster.getSProjctNm().isEmpty()){
            foCallback.OnFailed("Project name is not initialized");
            return;
        }else if (Integer.parseInt(foMaster.getCProjctTp()) < 0){
            foCallback.OnFailed("Project type is not initialized");
            return;
        }else if (foMaster.getDDueDatex().equalsIgnoreCase("1900-00-00")){
            foCallback.OnFailed("Due date is not initialized");
            return;
        }else if (foDetail.size() < 1){
            foCallback.OnFailed("Please add atleast one detail");
            return;
        }

        //initialize details to saving parameter and compute total for master
        List<EProjectDetail> laDetails = new ArrayList<>();
        double ldbl_pledge = 0.00, ldbl_paid = 0.00;

        boolean isDetailsOkay = true;
        for (int i = 0; i < foDetail.size(); i++){

            //check each details property
            if (foDetail.get(i).getSMemberID().isEmpty()){
                foCallback.OnFailed("Member ID is not initialized");
                isDetailsOkay = false;
                break;
            }else if (foDetail.get(i).getDPledgexx().isEmpty()){
                foCallback.OnFailed("Promise Date is not initialized");
                isDetailsOkay = false;
                break;
            }else if (foDetail.get(i).getNPledgexx().isEmpty()){
                foCallback.OnFailed("Promise Amount is not initialized");
                isDetailsOkay = false;
                break;
            }

            EProjectDetail loParam = new EProjectDetail(
                    foDetail.get(i).getSProjectCd(),
                    String.valueOf(i),
                    foDetail.get(i).getSMemberID(),
                    foDetail.get(i).getSORNoxx(),
                    foDetail.get(i).getDPledgexx(),
                    foDetail.get(i).getNPledgexx(),
                    foDetail.get(i).getNAmtPaidxx(),
                    GetCurrentDate(),
                    GetCurrentDateTime()
            );
            laDetails.add(loParam);

            ldbl_pledge += Double.parseDouble(foDetail.get(i).getNPledgexx());
        }

        //do not proceed if details not initialized properly
        if (!isDetailsOkay){
            return;
        }

        foMaster.setNTranTotl(String.valueOf(ldbl_pledge));
        foMaster.setNCollTotl(String.valueOf(ldbl_paid));

        poProjects.SaveProject(foMaster, laDetails).thenCompose(aBoolean -> {

            CompletableFuture<Boolean> chain = CompletableFuture.completedFuture(aBoolean);
            if (aBoolean){

                //if success saving, re initialze status from selection then update status
                foMaster.setCTranStat(fcStatus);
                chain = poProjects.UpdateProjectStatus(foMaster);
            }
            return chain;

        }).thenAccept(o -> {

            if (!o){
                foCallback.OnFailed(poProjects.message);
                return;
            }
            foCallback.OnSuccess();

        }).exceptionally(throwable -> {
            foCallback.OnFailed(throwable.getMessage());
            return null;
        });
    }

    public void DownloadProjects(String fsYearIDxx, String fsDfrom, String fsDto, OnTransaction foCallback){

        foCallback.OnLoad();
        poProjects.DownloadProjects(fsYearIDxx, fsDfrom, fsDto).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    foCallback.OnFailed(poProjects.getMessage());
                    return;
                }
                foCallback.OnSuccess();
            }
        });
    }

    public void DownloadProjectInformation(String fsProjectCd, OnDownload foCallback){

        foCallback.OnLoad();

        //store all threads into hash set, to execute one by one and avoid memory leakage
        HashSet<CompletableFuture<Boolean>> laTasks = new HashSet<>(
                List.of(
                        poProjects.DownloadProjectInfo(fsProjectCd),
                        poProjects.DownloadProjectDetails(fsProjectCd)
                )
        );

        //initialize task result holder
        CompletableFuture<Boolean> poResult = CompletableFuture.completedFuture(true);;
        for (CompletableFuture<Boolean> task : laTasks){

            poResult = poResult.thenCompose(aBoolean -> {
                if (!aBoolean) return CompletableFuture.completedFuture(false);
                return task;
            });
        }

        //get the result
        poResult.thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {
                if (!aBoolean){
                    foCallback.OnFinished(poProjects.getMessage());
                    return;
                }
                foCallback.OnFinished("Successfully downloaded information");
            }
        });
    }

    public void DownloadDashboardledges(String fsMemberID, OnTransaction foCallback){

        foCallback.OnLoad();
        poProjects.DownloadProjectMembers(fsMemberID).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    foCallback.OnFailed(poProjects.getMessage());
                    return;
                }
                foCallback.OnSuccess();
            }
        });

    }
}
