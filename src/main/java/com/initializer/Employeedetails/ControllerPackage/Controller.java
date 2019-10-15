package com.initializer.Employeedetails.ControllerPackage;

import com.initializer.Employeedetails.Tables.Employee;
import com.initializer.Employeedetails.Tables.Relation;
import com.initializer.Employeedetails.Tables.Info;
import com.initializer.Employeedetails.Repositories.EmployeeRepo;
import com.initializer.Employeedetails.Repositories.RelationRepo;
import com.sun.xml.internal.ws.encoding.soap.SOAP12Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.lang.*;
import java.util.*;

@RestController
public class Controller
{
    @Autowired
    public EmployeeRepo repository;
    @Autowired
    public RelationRepo repository1;

    @GetMapping(value = "/employees/{id}")
    @ResponseBody
    public ResponseEntity getOne(@PathVariable("id") int id)
    {   if(id<=0)
        {
            return new ResponseEntity("ID can not be 0 or Negative ",HttpStatus.BAD_REQUEST);
        }
        Map<String,Object> mp=new LinkedHashMap<>();
        //Fetching details of row with entered ID
        Employee user=repository.findById(id);
        if(user==null)
        {
            return new ResponseEntity("No record to display",HttpStatus.NOT_FOUND);
        }
        mp.put("employee",user);
        int  parentID=user.getManagerId();
        //Fetching details of Manager of ID entered
        Employee parent=repository.findById(parentID);
        if(parent!=null)
        {
            mp.put("manager",parent);
        }
        List <Employee> allColleagues=repository.findAllByManagerId(parentID, Sort.by("jid","name").ascending());
        if(!allColleagues.isEmpty())
        {
            //Fetching details of colleagues of entered ID
            List<Employee> colleague = coll(allColleagues, id);
            mp.put("colleagues", colleague);
        }

        //Fetching details of subordinates of id entered

        List<Employee> child=repository.findAllByManagerId(id,Sort.by("jid","name").ascending());
        if(!child.isEmpty())
        {
            mp.put("subordinates",child);
        }
        return new ResponseEntity(mp,HttpStatus.OK);
    }
    //Method to fetch colleagues(this method removes the details of row itself and returns only colleague's info)
    private List<Employee> coll(List<Employee> allColleagues, int id)
    {
        List<Employee> listColleague=new ArrayList<>();
        for(int i=0;i<allColleagues.size();i++)
        {
          Employee addColleague = allColleagues.get(i);
            //Relation one=repository1.findByJid(addColleague.getJid(),Sort.by("lid").ascending());
            if(addColleague.getId()!=id)
            {
                //Employee two=repository.findByJid(one.getJid());
                listColleague.add(addColleague);
            }
        }
        return listColleague;
    }


    @RequestMapping(value = "/employees",method = RequestMethod.GET)
    //Fetches detail of every entry in the table
    public ResponseEntity findAll()
    {
        Iterable<Employee> list= repository.findAll(Sort.by("jid","name"));
        return new ResponseEntity(list,HttpStatus.OK);
    }


    @PostMapping(path = "/employees")
    public ResponseEntity postDetail(@RequestBody Info user)
    {    if(user.getName()==null)
        {
        return new ResponseEntity("Name can not be null", HttpStatus.BAD_REQUEST);
        }
        if((user.getJobTitle()==null))
        {
            return new ResponseEntity("Job title can not be null", HttpStatus.BAD_REQUEST);
        }
        if(user.getManagerId()==null)
        {
            return new ResponseEntity("Manager Id can not be null.", HttpStatus.BAD_REQUEST);
        }
        if(!user.getName().matches("^[ A-Za-z]+$")){
            return new ResponseEntity("Name Invalid",HttpStatus.BAD_REQUEST);
        }

        Employee tDetails=new Employee();
        Relation fskey=repository1.findByJobTitle(user.getJobTitle());
        List<Employee> empList=repository.findAll(Sort.by("jid","name"));
        if(empList.isEmpty())
        {
            if(!user.getJobTitle().equals("Director"))
            {
                return new ResponseEntity("Only director can be added as first record",HttpStatus.BAD_REQUEST);
            }
            else
            {
                tDetails.setManagerId(-1);
                tDetails.setName(user.getName());
                Relation relation=repository1.findByJid(1,Sort.by("jobTitle"));
                tDetails.setJid(relation);
                repository.save(tDetails);
                return new ResponseEntity("record saved",HttpStatus.CREATED);
            }
        }

        //Set details that are entered by user in new entry
        if(!empList.isEmpty())
        {
            if(user.getJobTitle().equals("Director"))
            {
                return new ResponseEntity("Only one director can exist in one organisation",HttpStatus.BAD_REQUEST);
            }
        }
        if(user.getManagerId()<=0)
        {
            return new ResponseEntity("invalid manager ID",HttpStatus.BAD_REQUEST);
        }
        else
        {   int p=user.getManagerId();
            Employee em=repository.findById(p);
            if(em==null)
            {
                return new ResponseEntity("Manager Id is OUT",HttpStatus.BAD_REQUEST);
            }
            else
            {
                tDetails.setManagerId(user.getManagerId());
            }
        }
        tDetails.setName(user.getName());
        //tDetails.setManagerId(user.getManagerId());
        //Fetch designation from Relation table for auto assigning Job ID
        if (fskey==null)
        {
            return new ResponseEntity("Designation does not exist",HttpStatus.BAD_REQUEST);
//            Employee d=repository.findById(tDetails.getManagerId());
//            Relation r=repository1.findByJid(d.getJid().getJid(),Sort.by("jobTitle"));
//            Relation re=new Relation();
//            re.setJobTitle(user.getJobTitle());
//            re.setLid(((2*r.getLid())+10)/2);
//            repository1.save(re);
//            tDetails.setJid(re);
//            repository.save(tDetails);
        }

        Employee tPidDetails = repository.findById(tDetails.getManagerId());
        int tableJid = fskey.getJid();
        int userJid = tPidDetails.getJid().getJid();
        if (tableJid <= userJid) {
            return new ResponseEntity("Designation can not be same or higher", HttpStatus.BAD_REQUEST);
        }
        else
        {
            tDetails.setJid(fskey);
        }

            repository.save(tDetails);
            return new ResponseEntity(tDetails,HttpStatus.CREATED);
    }


    @DeleteMapping(value = "/employees/{id}")
    public ResponseEntity deleteOne(@PathVariable("id") int id)
    {   if(id<=0)
        {
            return new ResponseEntity("Id 0 does not exist.",HttpStatus.BAD_REQUEST);
        }
        if(!Integer.class.isInstance(id))
        {
            return new ResponseEntity("ID entered has to be an integer",HttpStatus.BAD_REQUEST);
        }
        //Getting row information of id that is to be deleted
        Employee user=repository.findById(id);
        if(user==null)
        {
            return new ResponseEntity("No id to delete",HttpStatus.NOT_FOUND);
        }
        if(user.getJobTitle().equals("Director"))
        {   List<Employee> mlist=repository.findAllByManagerId(1,Sort.by("name"));
            if(!mlist.isEmpty())
            {return new ResponseEntity("Director can't be deleted",HttpStatus.BAD_REQUEST);}
            else
            {
                return new ResponseEntity("Director deleted due to no child",HttpStatus.NOT_FOUND);
            }
        }
        //Relation duser=repository1.findByDesi()
        //String designation=duser.getDesi();
       // Checking if designation is director(director can not be deleted)
//        if(designation.equals("Director"))
//        {
//            return new ResponseEntity("Director can not be deleted",HttpStatus.BAD_REQUEST);
//        }
        int parent=user.getManagerId();
        //Finding children of row to be deleted
        List<Employee> listParent=repository.findAllByManagerId(id,Sort.by("jid","name").ascending());
        //Setting Parent id of each child to parent id of row to be deleted
        for(int i=0;i<listParent.size();i++)
        {
            Employee newParent=listParent.get(i);
            newParent.setManagerId(parent);
        }
        repository.deleteById(id);
        return new ResponseEntity("Record deleted",HttpStatus.NO_CONTENT);
    }


    @PutMapping(value = "/employees/{id}")
    public ResponseEntity updateOne(@PathVariable("id") int id,@RequestBody Info user)
    {   Employee update = repository.findById(id);
        if(user.isReplace())
        {
            if (update == null)
            {
            return new ResponseEntity("Unable to update. User with id " + id + " does not exist.", HttpStatus.BAD_REQUEST);
            }
            if(update.getJobTitle().equals("Director"))
            {
                return new ResponseEntity("director's designation can not be updated",HttpStatus.BAD_REQUEST);
            }
            Employee updateReplace=new Employee();

            //Set details in table entered by user
            if(user.getName()==null)
            {
                updateReplace.setName(update.getName());
                updateReplace.setManagerId(user.getManagerId());
                Relation fkey = repository1.findByJobTitle(user.getJobTitle());
                updateReplace.setJid(fkey);
                repository.save(updateReplace);
                int gID=updateReplace.getId();
                List<Employee> listParent=repository.findAllByManagerId(id,Sort.by("jid","name").ascending());
                for(int i=0;i<listParent.size();i++)
                {
                    Employee newParent=listParent.get(i);
                    newParent.setManagerId(gID);
                }
                repository.deleteById(id);

            }

            if(user.getManagerId()==null)
            {   Integer is=update.getManagerId();
                updateReplace.setName(user.getName());
                updateReplace.setManagerId(is);
                Relation fkey = repository1.findByJobTitle(user.getJobTitle());
                updateReplace.setJid(fkey);
                repository.save(updateReplace);
                int gID=updateReplace.getId();
                List<Employee> listParent=repository.findAllByManagerId(id,Sort.by("jid","name").ascending());
                for(int i=0;i<listParent.size();i++)
                {
                    Employee newParent=listParent.get(i);
                    newParent.setManagerId(gID);
                }
                repository.deleteById(id);
            }
            updateReplace.setName(user.getName());
            //String tableDesi=update.getDesi();
//            String userDesi=user.getDesi();
//            if(tableDesi.equals(userDesi))
//            {
//                updateReplace.setDesi(update.getDesi());
//            }
//            else
//            {
//                return new ResponseEntity("Designations of row being replaced have to be same",HttpStatus.BAD_REQUEST);
//            }
            updateReplace.setManagerId(user.getManagerId());
            Relation fkey = repository1.findByJobTitle(user.getJobTitle());
            updateReplace.setJid(fkey);
            repository.save(updateReplace);
            int gID=updateReplace.getId();
            List<Employee> listParent=repository.findAllByManagerId(id,Sort.by("jid","name").ascending());
            for(int i=0;i<listParent.size();i++)
            {
                Employee newParent=listParent.get(i);
                newParent.setManagerId(gID);
            }
            repository.deleteById(id);
            return new ResponseEntity(updateReplace,HttpStatus.OK);
        }
        else {

            if (update == null) {
                return new ResponseEntity("Unable to update. User with id " + id + " does not exist.", HttpStatus.BAD_REQUEST);
            }
            if(update.getJobTitle().equals("Director"))
            {
                return new ResponseEntity("director's designation can not be updated",HttpStatus.BAD_REQUEST);
            }


        //    String gName=user.getName();
//            if(update.getName().equals(gName) && update.getManagerId()==user.getManagerId())
//            {
//                return new ResponseEntity("Record Already Exists",HttpStatus.BAD_REQUEST);
//            }
//            else
//            {
                update.setName(user.getName());
            //String tableDesi=update.getDesi();
//            String userDesi=user.getDesi();
//            if(tableDesi.equals(userDesi))
//            {
//                update.setDesi(update.getDesi());
//            }
//            else
//            {
//                return new ResponseEntity("Designations of row being replaced have to be same",HttpStatus.BAD_REQUEST);
//            }
            //Set details in table entered by user
            update.setManagerId(user.getManagerId());//check same as above block
            //update.setDesi(update.getDesi());
            Relation fkey = repository1.findByJobTitle(user.getJobTitle());
            Employee gss = repository.findById(update.getManagerId());
            update.setJid(fkey);
            repository.save(update);
        }
        return new ResponseEntity(update, HttpStatus.OK);
    }
}