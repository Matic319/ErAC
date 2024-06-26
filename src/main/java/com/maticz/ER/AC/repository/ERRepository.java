package com.maticz.ER.AC.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ERRepository {

        @PersistenceContext
        EntityManager entityManager;

        public List<Object[]> ERQuery(){
            Query query = entityManager.createNativeQuery("\n" +
                    " select contactemail \n" +
                    "                    , visitdetail obiskal \n" +
                    "                    ,  concat(case when obiskov = 1  \n" +
                    "                                 then concat('Vesel sem, da si preizkusil naše sobe pobega. Kako smo se odrezali? Ti je bila izbrana soba všeč?',char(13),char(13),'Jaz sem velik ljubitelj sob pobega in sem preigral vse. Zato ti z veseljem svetujem :)') \n" +
                    "                     \n" +
                    "                                 when  obiskov > 1 and ((loc_orig = 3 and obiskov_lj<= 5) or (loc_orig = 6 and  obiskov_mb <=2 )) and first_sug_room is not null and second_sug_room is not null   \n" +
                    "                                 then 'Vidim, da si se odločil za ponoven obisk sobe pobega. Hvala. Sem poln idej in ti z veseljem predlagam katero sobo obišči naslednjič. Si za?' \n" +
                    "                                  \n" +
                    "                                 when obiskov > 1 and ((isnull(first_sug_dloc,0) <> loc_orig or isnull(second_sug_dloc,0) <> loc_orig )) \n" +
                    "                                 then concat('Mislim, da sem našel še enega ljubitelja naših sob pobega! Je tako? :)  Moram ti  povedati, da mi počasi zmanjkuje sob in idej kaj ti lahko še ponudim v ', case when loc_orig = 3 then 'Ljubljani. ' else 'Mariboru. ' end, 'Bi te zanimala tudi kakšna soba v ', case when loc_orig = 3 then 'Mariboru? ' else  'Ljubljani? ' end)  \n" +
                    "                     \n" +
                    "                                 when obiskov > 1 and ((isnull(first_sug_dloc,0) <> loc_orig and isnull(second_sug_dloc,0) <> loc_orig ))  \n" +
                    "                                 then concat('Sedaj je pa uradno... Zate nimam več sob v ', case when loc_orig = 3 then 'Ljubljani ' else 'Mariboru, ' end ,'ki bi bile primerne tvojemu okusu. Imam pa še par idej za ' ,case when loc_orig = 3 then 'Maribor, ' else  'Ljubljano, ' end ,'če si se pripravljen zapeljati tja.') \n" +
                    "                                 else 'Glej, glej kdo je bil spet v sobi pobega! :) Vedno me razveseli, ko vidim, še enega navdušenca nad sobami pobega. Potrebuješ nov predlog za obisk?' end  \n" +
                    "                    ,char(13),char(13),case when horror = 1 and min_age >=18   \n" +
                    "                         then'Vidim, da so ti všeč grozljive sobe. Bi ti bila všeč tudi kakšna avanturistična Prepričan sem, da bi ti bila spodnja predloga pisana na kožo:' \n" +
                    "                         when  min_age =0  \n" +
                    "                         then 'Imamo kar nekaj luštnih sob, ki bi ti znale biti všeč. Primerne so tudi za obisk z otroki, obenem pa niso otročje in boš v njih lahko užival tudi ti. Kaj praviš na:' \n" +
                    "                         else 'Glede na tvojo zadnjo izbiro bi ti predlagal:' end) nagovor \n" +
                    "                    , first_sug_room  prvi_predlog \n" +
                    "                    , case when first_sug_dloc = 3 then 'Lokacija: Ljubljana' else 'Lokacija: Maribor' end lokacija_prvi_predlog \n" +
                    "                    , concat('Težavnost: ',first_sug_difficulty,'/',10) tezavnost_prvi_predlog \n" +
                    "                    , first_sug_desc opis_prvi_predlog \n" +
                    "                    , second_sug_room drugi_predlog  \n" +
                    "                    , case when second_sug_room is null then null else case when second_sug_dloc = 3 then 'Lokacija: Ljubljana' else 'Lokacija: Maribor' end end lokacija_drugi_predlog \n" +
                    "                    , case when second_sug_room is null then null else concat('Težavnost: ',second_sug_difficulty,'/',10)end tezavnost_drugi_predlog \n" +
                    "                    , case when second_sug_room is null then null else second_sug_desc end opis_drugi_predlog \n" +
                    "                     \n" +
                    "                    from  \n" +
                    "                    (select a.contactemail, contacttype,idcontacttype, visitdetail,a.horror, min_age,  obiskov, obiskov_lj, obiskov_mb,loc_orig \n" +
                    "                    , max(case when renk = 1 then  next_room else null end) first_sug_room \n" +
                    "                    , max(case when renk = 1 then  difficulty else null end) first_sug_difficulty \n" +
                    "                    , max(case when renk = 1 then  loc_next else null end) first_sug_dloc \n" +
                    "                    , max(case when renk = 1 then  opis else null end)first_sug_desc \n" +
                    "                    , max(case when renk = 2 then  next_room else null end) second_sug_room \n" +
                    "                    , max(case when renk = 2 then  difficulty else null end)second_sug_difficulty \n" +
                    "                    , max(case when renk = 2 then  loc_next else null end)second_sug_dloc \n" +
                    "                    , max(case when renk = 2 then  opis else null end)second_sug_desc \n" +
                    "                    from  \n" +
                    "                    (select a.* \n" +
                    "                         , case when b.visitdetailid is null then 0 else 1 end obiskana \n" +
                    "                         , sum(case when b.visitdetailid is null then 0 else 1 end) over (partition by a.idcontact,a.IdTimeTableReservation) obiskov \n" +
                    "                         , sum(case when b.visitdetailid is null then 0 else case when b.idplace = 60 then 1 else 0 end end) over (partition by a.idcontact,a.IdTimeTableReservation) obiskov_lj \n" +
                    "                         , sum(case when b.visitdetailid is null then 0 else case when b.idplace = 360 then 1 else 0 end end) over (partition by a.idcontact,a.IdTimeTableReservation) obiskov_mb \n" +
                    "                         , case when b.visitdetailid is null and priority is not null then DENSE_RANK () over (partition by a.contactemail,a.IdTimeTableReservation, case when priority is null then 1 else 0 end order by case when b.visitdetailid is null then 0 else 1 end, priority ) else null end  renk \n" +
                    "                         , dense_rank () over (partition by a.contactemail order by visitdate desc,IdTimeTableReservation,idcontacttype,a.idcontact ) pravi_naslovnik \n" +
                    "                    from \n" +
                    "                    (select a.*, b.name Next_room, b.difficulty, visitdetailid, priority, b.idlocation loc_next, b.opis \n" +
                    "                    from \n" +
                    "                    (select a.idcontact,a.visitdate, c.contactemail,c.IdParentContactType,c.idcontacttype, a.idplace, c.contacttype,a.visitdetail,a.idlocation loc_orig, case when a.visitdetailid in (364,68,69,61) then 1 else 0 end horror,--51  \n" +
                    "                     a.IdTimeTableReservation \n" +
                    "                     ,  case when min (datediff(year, e.datebirth,a.visitdate) )  < 12 then 0 \n" +
                    "                             when min (datediff(year, e.datebirth,a.visitdate) )  < 18 then 12 \n" +
                    "                             when min (datediff(year, e.datebirth,a.visitdate) )  >= 12 then 18 else null end min_age \n" +
                    "                       from DWH_Fact_visit_Details a \n" +
                    "                          join  DWH_Fact_Contacts c  on a.idcontact =c.idcontact  \n" +
                    "                          left join DWH_Fact_visit_Details d on a.IdTimeTableReservation = d.IdTimeTableReservation \n" +
                    "                          left join DWH_Fact_Contacts e on d.idcontact = e.IdContact \n" +
                    "                      where a.idattraction = 10108  and isnull(c.idcountry,'SI') = 'SI' and a.idcontact >0 and a.visittypeid =1\n" +
                    "                       -- and a.idcontact = 478896   and a.IdTimeTableReservation = 13600008389256 \n" +
                    "                       -- and a.IdTimeTableReservation in (--10600007839554,10600003559488) \n" +
                    "                       and cast(a.visitdate as date) = cast(dateadd(day,-1,getdate()) as date) \n" +
                    "                    group by a.idcontact,a.visitdate, c.contactemail,c.IdParentContactType,c.idcontacttype,a.idplace, c.contacttype,a.visitdetail,a.idlocation, case when a.visitdetailid in (364,68,69,61) then 1 else 0 end ,a.IdTimeTableReservation) a \n" +
                    "                    left join AC_EscapeRooms_Email_Focus b on a.idplace = b.idplace and min_age =age_limit and a.horror = b.horror) a \n" +
                    "                    left join (select distinct contactemail, visitdetailid,idplace from DWH_Fact_visit_Details a, DWH_Fact_Contacts b where idattraction = 10108 and a.idcontact = b.idcontact) b on a.contactemail = b.contactemail and a.visitdetailid = b.visitdetailid \n" +
                    "                    )a \n" +
                    "                    where renk < 3 and pravi_naslovnik = 1 \n" +
                    "                    group by a.contactemail, contacttype,idcontacttype, visitdetail,a.horror, min_age,  obiskov, obiskov_lj, obiskov_mb,loc_orig)a " );

            List<Object[]> resultList = query.getResultList();

            return resultList;
        }


    }
