path <- "Y:/Private/LysateArray/DorisSiwak/Feiller/Feiler txt files"  
results <- "Y:/Private/LysateArray/DorisSiwak/Feiller/Feiler results"                                              
designparams <- RPPADesignParams(grouping="blockSample", center=FALSE, controls=list("control"))
fitparams <- RPPAFitParams(measure = "Mean.Total", ignoreNegative=FALSE, method='robust', warnLevel=-1)
rm(fitset)                       
fitset <- RPPAFitDir(path, designparams, fitparams)
write.summary(fitset, file = 'supercurve', path = results, normalize = 'median', graphs=TRUE)