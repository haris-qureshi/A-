import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;





//javac find_input.java
//java find_input

// or just hit f5 to run and compile i think

class find_input {
    static String fileName, orgin, dest, heuristic = null;
    //array that holds the map/graph
    static List<ucs_node> nodes = new ArrayList<ucs_node>();
    //using the visted list we can rebuild the path 
    static List<smaller_node> visted = new ArrayList<smaller_node>(); // when we vist a node for the first time we also expand that node 
    // just to keep track of all the generated nodes
    static List<String> gen = new ArrayList<String>();
    static float ans_cost =0;
    //global fringe is best fringe
    static List<smaller_node> fringe = new ArrayList<smaller_node>();



    public static void main(String[] args) {
       
        

        // this is just error checking to make sure something was put in 
        // also loading the variables with their information        
        if (args.length == 0)
        {
            System.out.println("args is empty");
            //System.exit(0);
        }
        else if (args.length == 4) 
        {
            fileName = args[0];
            orgin = args[1];
            dest = args[2];
            heuristic = args[3];
        }
        else 
        {
            fileName = args[0];
            orgin = args[1];
            dest = args[2];
        }

        if(heuristic == null)
        {  //System.out.printf("%s %s\n",orgin,dest);
            
            Ucs_read();
            smaller_node temp = new smaller_node();
            temp.name = orgin;
            Tree(temp,0);
            System.out.printf("nodes expanded: %d\n", visted.size());
            System.out.printf("nodes generated: %d\n", gen.size());
            if (ans_cost !=0)
            {
                System.out.println("distance: "+ans_cost+"km");
                System.out.println("route:");
                List<smaller_node> oof = generate_path();
                for (smaller_node v : oof)
                {
                    
                    System.out.printf("%s %s %.1fkm\n", v.parent,v.name,v.cost);
                    
                }
                
            
            }
            
            else
            {
                System.out.println("distance: infinity");
                System.out.println("route:\nnone\n");
            }
            
        }
    }



    public static void Tree(smaller_node source, float cost)
    {
        //System.out.printf("current node:%s\n parent:%s\n cost:%d\n",source.name,source.parent,source.cost);
        //if the current cost is greater then the answer cost it's just better to leave
        /*
        if(ans_cost != 0 && ans_cost < cost )
        {
            return;
        }
        */
        // check if this is the final state of the tree
        if (source.name.equals(dest))
        {
            
            //System.out.printf("\n\n%s %s %d\n\n",source.parent, source.name, source.cost);
            // check if this is the cheapest path we can have
            if (ans_cost > cost)
            {
                ans_cost = cost;

                return;
            }
            else if (ans_cost == 0)
            {
                ans_cost = cost;
                return;
            }

            //System.out.println("reached the end");
            if (!visted.contains(source))
            {
                visted.add(source);
            }
            return;
        }

        //if we arrive to a node we have already visted return this is the base case
        for(smaller_node v: visted)
        {
            if (v.name.equals(source.name))
                 {
                       //System.out.println("been there done that");
                        return;
                }
          
        }
                
        visted.add(source);
        
        

        //List<smaller_node> fringe = new ArrayList<smaller_node>();

        


        // checking all the cites that are connected to the this city basically expanding the node
        for (ucs_node t: nodes)
        {
            if (t.name.equals(source.name))
            {  
                //System.out.printf("%s %s %d\n", source.name, t.reachable_city, t.cost);
                smaller_node temp = new smaller_node();
                temp.parent = source.name;
                temp.name = t.reachable_city;
                temp.cost = t.cost+cost;
                fringe.add(temp);
                //System.out.printf("%s %s %d\n",temp.parent,temp.name,temp.cost);
                if(!gen.contains(temp.parent))
                {
                    gen.add(temp.parent);
                }
            }
            else if (t.reachable_city.equals(source.name))
            {
                
                //System.out.printf("%s %s %d\n", source.name, t.name, t.cost);
                smaller_node temp = new smaller_node();
                temp.parent = t.reachable_city;
                temp.name = t.name;
                temp.cost = t.cost+cost;
                //System.out.printf("%s %s %d\n",temp.parent,temp.name,temp.cost);
                fringe.add(temp);
                if(!gen.contains(temp.parent))
                {
                    gen.add(temp.parent);
                }
            }
        }

        smaller_node[] sorted_fringe = sort();

        

        /*
        System.out.println("fringe");
        for(smaller_node v : sorted_fringe)
        {
            System.out.printf("\n\n%s,%s,%s",v.parent,v.name,v.cost );
        }
        System.out.println("\n\n");
        */
        
        
        //traversing the sorted fringe
        smaller_node t = new smaller_node();
        for(int i =0;i < sorted_fringe.length; i++)
        {
           // System.out.println("up next");
            //System.out.printf("\n %s,%s %s \n",sorted_fringe[i].parent,sorted_fringe[i].name,sorted_fringe[i].cost );
            Tree(sorted_fringe[i],sorted_fringe[i].cost);
            smaller_node temp = sorted_fringe[i];
            t = temp;
            //pop this element from the fringe
            fringe.remove(t);
            
        }
        //fringe.remove(t);
        

    }




    public static void Ucs_read()
    {
        try
        {
            String tempname,tempdest;
            float tempcost;
            

            File input_file = new File(fileName);
            Scanner input = new Scanner(input_file);

            for (int i =0;input.hasNext();i++)
            {
                ucs_node temp = new ucs_node();
                tempname = input.next();
                //checks if this is the end of the line 
                if (tempname.equals("END"))
                {
                    break;
                }
                tempdest = input.next();
                //double checking to make 100 percent sure
                if (tempdest.equals("END"))
                    break;

                tempcost = input.nextFloat();
                
               //System.out.printf("%s %s %s\n", tempname,tempdest, tempcost);
                temp.name = tempname;
                temp.reachable_city = tempdest;
                temp.cost = tempcost;

                // incase the source and destination nodes already have a path with no nodes in between
                if((orgin.equals(tempname)) && (dest.equals(tempdest)))
                {
                    System.out.printf("nodes expanded: 1\nnodes generated: 0\ndistance: %.1fkm\nroute:\n%s to %s %.1fkm\n",tempcost,tempname,tempdest,tempcost);
                    System.exit(0);
                }


                //System.out.println(temp.to_string());
                nodes.add(temp);

            }   

            input.close();




        }
        catch(Exception e)
        {
            System.out.println(e);
            System.out.println("there was an error");
        }
        return;
    }



    // this is used to read in the values from the file
    public static class ucs_node
    {
        public String name ="", reachable_city = "";
        public float cost = 0;
        public  String to_string ()
        {
            String send = name +" "+ reachable_city + " " + cost; 
            return send;
        }
    }
    

    // the actual nodes used by the usc
    public static class smaller_node
    {
        public String name="";
        public String parent = ""; // this is how we build the path and get the total cost
        public float cost = 0;

    }

    public static smaller_node[] sort()
    {
        smaller_node[] sorted_fringe = fringe.toArray(new smaller_node[fringe.size()]);

        //sorting the fringe this is a local fringe
        for(int i =0; i < fringe.size()-1; i++)
        {
            for (int j = i+1; j <= fringe.size()-1-i; j++)
            {
            if(sorted_fringe[i].cost > sorted_fringe[j].cost)
            {
                smaller_node temp = sorted_fringe[j];
                sorted_fringe[j]= sorted_fringe[i];
                sorted_fringe[i] = temp;
            }
            }
        }

        return sorted_fringe;
        
    }


    public static List<smaller_node> generate_path()
    {
        smaller_node[] sorted_fringe = visted.toArray(new smaller_node[visted.size()]);
        List<smaller_node> send = new ArrayList<smaller_node>();

        smaller_node temp = sorted_fringe[sorted_fringe.length-1];
        send.add(temp);

        //System.out.println(temp.parent+" "+temp.name+" "+temp.cost);
        for (int i =sorted_fringe.length-2; i != 0; i--)
        {
            // the last answer in the visted array will be the answer
            
           if (temp.parent.equals(sorted_fringe[i].name) && (i != 0))
            {
                //System.out.println(sorted_fringe[i].parent+" "+sorted_fringe[i].name+" "+sorted_fringe[i].cost);
                temp = sorted_fringe[i];
                send.add(0,sorted_fringe[i]);
            }
            

        }
        
        for(ucs_node usc : nodes)
        {
            for (int i =0; i < send.size();i++)
            {

                if (usc.name.equals(send.get(i).parent) && usc.reachable_city.equals(send.get(i).name))
                {
                    send.get(i).cost = usc.cost;
                }
                else if (usc.name.equals(send.get(i).name) && usc.reachable_city.equals(send.get(i).parent))
                {
                    send.get(i).cost = usc.cost;
                }
            
            }
        
        }
        return send;
    }


}

