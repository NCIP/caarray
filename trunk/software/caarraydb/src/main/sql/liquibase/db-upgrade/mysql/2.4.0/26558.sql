-- update the probes to be physical probes
update design_element de, array_design ad, array_design_design_file addf, caarrayfile f 
  set de.discriminator='PP', de.physicalprobe_details_id = de.logicalprobe_details_id , de.logicalprobe_details_id = null
  where 
    de.logicalprobe_details_id = ad.design_details and addf.array_design = ad.id and addf.design_file = f.id 
    and de.discriminator = 'LP' and f.type = 'ILLUMINA_DESIGN_CSV'