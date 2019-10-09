package com.initializer.Employeedetails.result;

import com.initializer.Employeedetails.GS.Employee;
import com.initializer.Employeedetails.GS.Relation;
import com.initializer.Employeedetails.GS.Info;
import com.initializer.Employeedetails.Inter.EmployeeRepo;
import com.initializer.Employeedetails.Inter.RelationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class Controller
{
    @Autowired
    public EmployeeRepo repository;
    @Autowired
    public RelationRepo repository1;
    @RequestMapping(value = "/rest/employees/get/{id}",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getOne(@PathVariable("id") int id)
    {
        Map<String,Object> mp=new LinkedHashMap<>();
        //Fetching details of row with entered ID
        Employee user=repository.findById(id);
        if(user==null)
        {
            return new ResponseEntity("No record to display",HttpStatus.BAD_REQUEST);
        }
        mp.put("Employee",user);
        int  parentID=user.getpID();
        //Fetching details of Manager of ID entered
        Employee parent=repository.findById(parentID);
        if(parentID!=0)
        {
            mp.put("Manager",parent);
        }
        List <Employee> allColleagues=repository.findAllByPID(parentID, Sort.by("jid","name").ascending());
        //Fetching details of colleagues of entered ID
        List<Employee> colleague= coll(allColleagues,id);
        mp.put("Colleagues : ",colleague);
        //Fetching details of subordinates of id entered
        List<Employee> child=repository.findAllByPID(id,Sort.by("jid","name").ascending());
        mp.put("ReportingTo",child);
        return new ResponseEntity(mp,HttpStatus.OK);
    }
    //Method to fetch colleagues(this method removes the details of row itself and returns only colleague's info)
    private List<Employee> coll(List<Employee> allColleague, int id)
    {
        List<Employee> listColleague=new ArrayList<>();
        for(int i=0;i<allColleague.size();i++)
        {
            Employee addColleague=allColleague.get(i);
            if(addColleague.getId()!=id)
            {
                listColleague.add(addColleague);
            }
        }
        return listColleague;
    }


    @RequestMapping(value = "/rest/employees/get",method = RequestMethod.GET)
    //Fetches detail of every entry in the table
    public ResponseEntity findAll()
    {
        Iterable<Employee> list= repository.findAll();
        return new ResponseEntity(list,HttpStatus.OK);
    }


    @PostMapping(path = "/rest/employees/post",consumes = {"application/json"})
    public ResponseEntity postDetail(@RequestBody Info user)
    {
        if((user.getName()==null) || (user.getDesi()==null) || (user.getpID()==null))
        {
            return new ResponseEntity("Any of the required values can not be null. Enter Name,Designation and PID and try again.",HttpStatus.BAD_REQUEST);
        }
        Employee tDetails=new Employee();
        //Set details that are entered by user in new entry
        tDetails.setName(user.getName());
        tDetails.setpID(user.getpID());
        //Fetch designation from Relation table for auto assigning Job ID
        Relation fkey = repository1.findByDesi(user.getDesi());
        tDetails.setDesi(user.getDesi());
        tDetails.setJid(fkey);
        Employee tPidDetails=repository.findById(tDetails.getpID());
        int tableJid=fkey.getJid();
        int userJid=tPidDetails.getJid().getJid();
        if(tableJid<=userJid)
        {
            return new ResponseEntity("Designation can not be same or higher",HttpStatus.BAD_REQUEST);
        }
        repository.save(tDetails);
        return new ResponseEntity(tDetails,HttpStatus.OK);
    }


    @DeleteMapping(value = "/rest/employees/delete/{id}")
    public ResponseEntity deleteOne(@PathVariable("id") int id)
    {
        //Getting row information of id that is to be deleted
        Employee user=repository.findById(id);
        String designation=user.getDesi();
        //Checking if designation is director(director can not be deleted)
        if(designation.equals("Director"))
        {
            return new ResponseEntity("Director can not be deleted",HttpStatus.BAD_REQUEST);
        }
        int parent=user.getpID();
        //Finding children of row to be deleted
        List<Employee> listParent=repository.findAllByPID(id,Sort.by("jid","name").ascending());
        //Setting Parent id of each child to parent id of row to be deleted
        for(int i=0;i<listParent.size();i++)
        {
            Employee newParent=listParent.get(i);
            newParent.setpID(parent);
        }
        repository.deleteById(id);
        return new ResponseEntity("Record deleted",HttpStatus.OK);
    }


    @PutMapping(value = "/rest/employees/put/{id}")
    public ResponseEntity updateOne(@PathVariable("id") int id,@RequestBody Info user)
    {   Employee update = repository.findById(id);
        if(user.isReplace())
        {   if (update == null) {
            return new ResponseEntity("Unable to update. User with id " + id + " does not exist.", HttpStatus.NOT_FOUND);
        }
            Employee updateReplace=new Employee();
            String gName=user.getName();
            if(update.getName().equals(gName) && update.getpID()==user.getpID())
            {
                return new ResponseEntity("Record Already Exists",HttpStatus.BAD_REQUEST);
            }
            //Set details in table entered by user
            updateReplace.setName(user.getName());
            String tableDesi=update.getDesi();
            String userDesi=user.getDesi();
            if(tableDesi.equals(userDesi))
            {
                updateReplace.setDesi(update.getDesi());
            }
            else
            {
                return new ResponseEntity("Designations of row being replaced have to be same",HttpStatus.BAD_REQUEST);
            }
            updateReplace.setpID(update.getpID()); //create check that pid can not be lower than pid of its child
            Relation fkey = repository1.findByDesi(update.getDesi());
            updateReplace.setJid(fkey);
            repository.save(updateReplace);
            int gID=updateReplace.getId();
            List<Employee> listParent=repository.findAllByPID(id,Sort.by("jid","name").ascending());
            for(int i=0;i<listParent.size();i++)
            {
                Employee newParent=listParent.get(i);
                newParent.setpID(gID);
            }
            repository.deleteById(id);
            return new ResponseEntity(updateReplace,HttpStatus.OK);
        }
        else {

            if (update == null) {
                return new ResponseEntity("Unable to update. User with id " + id + " does not exist.", HttpStatus.NOT_FOUND);
            }
            String gName=user.getName();
            if(update.getName().equals(gName) && update.getpID()==user.getpID())
            {
                return new ResponseEntity("Record Already Exists",HttpStatus.BAD_REQUEST);
            }
            else
            {
                update.setName(user.getName());
            }
            String tableDesi=update.getDesi();
            String userDesi=user.getDesi();
            if(tableDesi.equals(userDesi))
            {
                update.setDesi(update.getDesi());
            }
            else
            {
                return new ResponseEntity("Designations of row being replaced have to be same",HttpStatus.BAD_REQUEST);
            }
            //Set details in table entered by user
            update.setpID(update.getpID());//check same as above block
            update.setDesi(update.getDesi());
            Relation fkey = repository1.findByDesi(update.getDesi());
            Employee gss = repository.findById(update.getpID());
            update.setJid(fkey);
            repository.save(update);
        }
        return new ResponseEntity(update, HttpStatus.OK);
    }
}