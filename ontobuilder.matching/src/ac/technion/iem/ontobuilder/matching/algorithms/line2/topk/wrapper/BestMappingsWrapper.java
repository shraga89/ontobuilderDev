package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.wrapper;

import java.util.*;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.stablemarriage.StableMarriageWrapper;
import ac.technion.iem.ontobuilder.matching.meta.match.AbstractMapping;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchedAttributePair;
import ac.technion.iem.ontobuilder.matching.utils.SchemaTranslator;


/**
 * <p>Title: Best Mappings Wrapper</p>
 * <p>Description: this wrapper was created during research done by Anan Marie 
 * and is out of sync with the latest iteration of ontobuilder - major open issue</p>
 * @author Anan Marie
 * @version 1.0
 * @deprecated use the ontobuilder research environment second line matcher wrappers instead
 */

public class BestMappingsWrapper {

  public BestMappingsWrapper() {
  }

  public static SchemaTranslator GetBestMapping(String mappingAlgoNameIn) {
    if (mappingAlgoNameIn == MappingAlgorithms.MAX_WEIGHT_BIPARTITE_GRAPH) {
      return GetBestMappingByMwbg();
    }
    if (mappingAlgoNameIn == MappingAlgorithms.STABLE_MARRIAGE) {
      return GetBestMappingBySm();
    }
    if (mappingAlgoNameIn == MappingAlgorithms.DOMINANTS) {
      return GetBestMappingByDom();
    }
    if (mappingAlgoNameIn ==
        MappingAlgorithms.MAX_WEIGHT_BIPARTITE_GRAPH_STABLE_MARRIAGE_INTERSECTION) {
      return GetBestMappingByIntersection();
    }
    if (mappingAlgoNameIn ==
        MappingAlgorithms.MAX_WEIGHT_BIPARTITE_GRAPH_STABLE_MARRIAGE_UNION) {
      return GetBestMappingByUnion();
    }/*
    if (mappingAlgoNameIn == MappingAlgorithms.BETA_DIST_MODEL_MAPPING) {
      return GetBestMappingByBetaDist();
    }
    if (mappingAlgoNameIn == MappingAlgorithms.FILTERED_MAX_WEIGHT_MAPPING) {
      return GetBestMappingByFilteredMw();
    }*/
    return null;
  }

  /**
   * @deprecated 
   * @param mappingAlgoNameIn
   * @param candidate
   * @param target
   * @return
   */
  public static SchemaTranslator GetBestMapping(String mappingAlgoNameIn, Ontology candidate, Ontology target){

//    if(mappingAlgoNameIn == MappingAlgorithms.NAIVE_BAYES) {
//      NaiveBayesMapping nbm = new NaiveBayesMapping();
//      return nbm.getBestMapping(candidate,target);
//    }
    return null;
  }


  public static SchemaTranslator GetBestMappingTopK(int kIn){
    try {
      SchemaMatchingsWrapper smw = new SchemaMatchingsWrapper(matchMatrix);
      return smw.getKthBestMatching(kIn);
    }
    catch (Exception e) {
      return null;
    }

  }

  private static SchemaTranslator  GetBestMappingByMwbg(){
    try {
      SchemaMatchingsWrapper smw = new SchemaMatchingsWrapper(matchMatrix);
      return smw.getBestMatching();
    }
    catch (Exception e) {
      return null;
    }
  }

  private static SchemaTranslator  GetBestMappingByFilteredMw(){
    SchemaTranslator bmByUnion = GetBestMappingByUnion();
//    SchemaTranslator bmByDominants = GetBestMappingByDom();
    Filter(null,bmByUnion);
    try {
      SchemaMatchingsWrapper smw = new SchemaMatchingsWrapper(matchMatrix);
      SchemaTranslator bmByFilteredMw = smw.getBestMatching();
      return bmByFilteredMw;
    }
    catch (Exception e) {
      return null;
    }
  }





  private static SchemaTranslator GetBestMappingBySm(){
    StableMarriageWrapper m_StableMarriageWrapper = new StableMarriageWrapper();
    return new SchemaTranslator(m_StableMarriageWrapper.runAlgorithm(matchMatrix, null, null));
  }

  private static SchemaTranslator GetBestMappingByDom(){
    return CalculateDominantPairs(matchMatrix);
  }

  private static SchemaTranslator GetBestMappingByIntersection(){
    AbstractMapping em = GetBestMappingByMwbg();
    AbstractMapping tkm = GetBestMappingBySm();
    ArrayList<MatchedAttributePair> m = intersectMappings(em, tkm);
    MatchedAttributePair[] obArrayMatchedPair = new MatchedAttributePair[m.size()];
    obArrayMatchedPair = (MatchedAttributePair[]) m.toArray(obArrayMatchedPair);
    SchemaTranslator obSchemaTranslator = new SchemaTranslator();
    obSchemaTranslator.setSchemaPairs(obArrayMatchedPair);
    return obSchemaTranslator;

  }

  private static SchemaTranslator GetBestMappingByUnion(){
    SchemaTranslator sTranslator1 = GetBestMappingByMwbg();
    SchemaTranslator sTranslator2 = GetBestMappingBySm();
    ArrayList<MatchedAttributePair> m1 = null;
    ArrayList<MatchedAttributePair> m2 = null;
    ArrayList<MatchedAttributePair> m = null;
    if(sTranslator1 != null)
      m1 = getAttributePairsAsArrayList(sTranslator1);
    if(sTranslator2 != null)
      m2 = getAttributePairsAsArrayList(sTranslator2);
    if(m1 == null)
      m = m2;
    else if(m2 == null)
      m = m1;
    else
      m = plus(m1, m2);
    MatchedAttributePair[] obArrayMatchedPair = new MatchedAttributePair[m.size()];
    obArrayMatchedPair = (MatchedAttributePair[]) m.toArray(obArrayMatchedPair);
    SchemaTranslator obSchemaTranslator = new SchemaTranslator();
    obSchemaTranslator.setSchemaPairs(obArrayMatchedPair);

    return obSchemaTranslator;

  }

  private static SchemaTranslator GetBestMappingByUnion(SchemaTranslator sTranslator1, SchemaTranslator sTranslator2){
    ArrayList<MatchedAttributePair> m1 = null;
    ArrayList<MatchedAttributePair> m2 = null;
    ArrayList<MatchedAttributePair> m = null;
    if(sTranslator1 != null)
      m1 = getAttributePairsAsArrayList(sTranslator1);
    if(sTranslator2 != null)
      m2 = getAttributePairsAsArrayList(sTranslator2);
    if(m1 == null)
      m = m2;
    else if(m2 == null)
      m = m1;
    else
      m = plus(m1, m2);
    MatchedAttributePair[] obArrayMatchedPair = new MatchedAttributePair[m.size()];
    obArrayMatchedPair = (MatchedAttributePair[]) m.toArray(obArrayMatchedPair);
    SchemaTranslator obSchemaTranslator = new SchemaTranslator();
    obSchemaTranslator.setSchemaPairs(obArrayMatchedPair);

    return obSchemaTranslator;

  }


  private static SchemaTranslator GetBestMappingByBetaDist(){
    SchemaTranslator mapping = new SchemaTranslator();
//    double a_beta_param_neg = 2.6452;
//    double b_beta_param_neg = 16.3139;
//    int negSampleSize = 104503;

//    double a_beta_param_pos = 3.2205;
//    double b_beta_param_pos = 2.3844;
//    int posSampleSize = 1821;
    double simDegree;
    double maxValue = 0;
//    double probabilityToBePos;
//    double probabilityToBeNeg;
    double eps = 2.2204e-016;
    MatchedAttributePair map;
//    int negSize = 104503;
//    int posSize = 1821;
    ArrayList<MatchedAttributePair> alFilteredMatchingResult = new ArrayList<MatchedAttributePair>();
//    BetaDistribution betaDistPos = new BetaDistribution(a_beta_param_pos,b_beta_param_pos);
//    BetaDistribution betaDistNeg = new BetaDistribution(a_beta_param_neg,b_beta_param_neg);
    int row = matchMatrix.getRowCount();
    int col = matchMatrix.getColCount();
    for (int r = 0; r < row; ++r) {
      for (int c = 0; c < col; ++c) {
        maxValue = Math.max(maxValue,matchMatrix.getMatchConfidenceAt(r,c));
      }
    }

    ArrayList<Term> candTerms =  matchMatrix.getCandidateTerms();
    ArrayList<Term> targetTerms = matchMatrix.getTargetTerms();
    
    for (Term t : targetTerms) {
      for (Term c : candTerms) {
        simDegree = matchMatrix.getMatchConfidence(c, t);

        if((simDegree/maxValue) >= 0.975){
          map = new MatchedAttributePair(c.getName(), t.getName(), 1.0,c.getId(),t.getId());
          alFilteredMatchingResult.add(map);
        }else{
          if(simDegree == 0.0)
            simDegree += eps;
//          probabilityToBeNeg = betaDistNeg.probability(simDegree);
//          probabilityToBePos = betaDistPos.probability(simDegree/maxValue);
//          if(probabilityToBePos >= probabilityToBeNeg){
//            map = new MatchedAttributePair(candTerms[c], targetTerms[r], 1.0);
//            alFilteredMatchingResult.add(map);
//          }
        }
      }
    }
    MatchedAttributePair[] obArrayMatchedPair = new MatchedAttributePair[alFilteredMatchingResult.size()];
    obArrayMatchedPair = (MatchedAttributePair[]) alFilteredMatchingResult.toArray(obArrayMatchedPair);
    mapping.setSchemaPairs(obArrayMatchedPair);

    return mapping;
  }

  private static ArrayList<MatchedAttributePair> minus(ArrayList<MatchedAttributePair> a, ArrayList<MatchedAttributePair> b) {
    ArrayList<MatchedAttributePair> minus = new ArrayList<MatchedAttributePair>();
    for (MatchedAttributePair p : a)
    {  if (!b.contains(p)) {
        minus.add(p);
      }
    }
    return minus;
  }

  private static ArrayList<MatchedAttributePair> plus(ArrayList<MatchedAttributePair> a, ArrayList<MatchedAttributePair> b) {
    ArrayList<MatchedAttributePair> plus = new ArrayList<MatchedAttributePair>();
    
    for (MatchedAttributePair p : a)
    {
      if (!plus.contains(p)) {
        plus.add(p);
      }
    }
    
    for (MatchedAttributePair p : b)
    {
      if (!plus.contains(p)) {
        plus.add(p);
      }
    }
    return plus;
  }

  private static ArrayList<MatchedAttributePair> intersectMappings(AbstractMapping stNext, AbstractMapping stPrevious) {
    ArrayList<MatchedAttributePair> a = new ArrayList<MatchedAttributePair>(), b = new ArrayList<MatchedAttributePair>();

    int iStNextSize = stNext.getMatchedAttributesPairsCount();
    for (int i = 0; i < iStNextSize; i++) {
      a.add(stNext.getMatchedAttributePair(i));
    }

    int iStPreviousSize = stPrevious.getMatchedAttributesPairsCount();
    for (int j = 0; j < iStPreviousSize; j++) {
      b.add(stPrevious.getMatchedAttributePair(j));
    }
    return minus(a, minus(a, b));
  }
/*
  private double CalculateRecall(AbstractMapping em, AbstractMapping tkm) {
    double b = intersectMappings(em, tkm).size();
    double a = em.getMatchedAttributesPairsCount();
    if (a != 0) {
      return (b / a);
    }
    else {
      return 1;
    }

  }

  private double CalculatePrecision(AbstractMapping em, AbstractMapping tkm) {
    double b = intersectMappings(em, tkm).size();
    double c = tkm.getMatchedAttributesPairsCount();
    if (c != 0) {
      return (b / c);
    }
    else {
      return 1;
    }

  }
*/
  private static ArrayList<MatchedAttributePair> getAttributePairsAsArrayList(AbstractMapping stNext) {
    ArrayList<MatchedAttributePair> a = new ArrayList<MatchedAttributePair>();
    int iStNextSize = stNext.getMatchedAttributesPairsCount();
    for (int i = 0; i < iStNextSize; i++) {
      a.add(stNext.getMatchedAttributePair(i));
    }
    return a;
  }

  private static SchemaTranslator CalculateDominantPairs(MatchMatrix matrix) {
    SchemaTranslator mapping = new SchemaTranslator();
    ArrayList<MatchedAttributePair> m_alFilteredMatchingResult = new ArrayList<MatchedAttributePair>();

    Hashtable<Term, Double> hMaxInTarget = GetMaxInTargetTerms(matrix);
    Hashtable<Term, Double> hMaxInCandidate = GetMaxInCandidateTerms(matrix);

    for (Term t : matrix.getTargetTerms()) {
      for (Term c : matrix.getCandidateTerms()) {
        double dConfidence = matrix.getMatchConfidence(c,t);
        Double d1 = (Double) (hMaxInTarget.get(t));
        double d1value = d1.doubleValue();
        Double d2 = (Double) (hMaxInCandidate.get(c));
        double d2value = d2.doubleValue();
        if ( (dConfidence == d1value) && (dConfidence == d2value)) {
          MatchedAttributePair map = new MatchedAttributePair(c.getName(), t.getName(), 1.0, c.getId(), t.getId());
          m_alFilteredMatchingResult.add(map);
        }
      }
    }
    MatchedAttributePair[] obArrayMatchedPair = new MatchedAttributePair[
        m_alFilteredMatchingResult.size()];
    obArrayMatchedPair = (MatchedAttributePair[]) m_alFilteredMatchingResult.
        toArray(obArrayMatchedPair);

    mapping.setSchemaPairs(obArrayMatchedPair);

    return mapping;
  }

  private static Hashtable<Term, Double> GetMaxInTargetTerms(MatchMatrix matrix) {
    Hashtable<Term, Double> hash = new Hashtable<Term, Double>();
    ArrayList<Term> candidateTerms = matrix.getCandidateTerms();
    ArrayList<Term> targetTerms = matrix.getTargetTerms();

    for (Term t : targetTerms) {
      double max = 0.0;
      for (Term c : candidateTerms ) {
        double dConfidence = matrix.getMatchConfidence(c, t);
        if (dConfidence > max) {
          max = dConfidence;
        }
      }
      hash.put(t, new Double(max));
    }
    return hash;
  }

  private static Hashtable<Term, Double> GetMaxInCandidateTerms(MatchMatrix matrix) {
    Hashtable<Term, Double> hash = new Hashtable<Term, Double>();
    
    for (Term c : matrix.getCandidateTerms()) {
      double max = 0;
      for (Term t : matrix.getTargetTerms()) {
        double dConfidence = matrix.getMatchConfidence(c,t);
        if (dConfidence > max) {
          max = dConfidence;
        }
      }
      hash.put(c, new Double(max));
    }
    return hash;
  }

  private static void Filter(SchemaTranslator mappingsToBeOned, SchemaTranslator mappingsToBeZeroed){
    //MatchedAttributePair[] matchedPairs = mappingsToBeOned.getMatchedPairs();
    MatchedAttributePair map;
    for(Term t : matchMatrix.getTargetTerms()){
      for(Term c : matchMatrix.getCandidateTerms()){
        map = new MatchedAttributePair(c.getName(),t.getName(),1.0,c.getId(), t.getId());
        /*if(mappingsToBeOned.isExist(map)){
          matchMatrix.setMatchConfidenceAt(i,j,1.0);
        }*/
        //else{
        if(!mappingsToBeZeroed.isExist(map)){
          matchMatrix.setMatchConfidence(c,t,0.0);
        }
        //}
      }
    }
  }

  public static MatchMatrix matchMatrix = null;

  public static void main(String[] args) {
  }

}
