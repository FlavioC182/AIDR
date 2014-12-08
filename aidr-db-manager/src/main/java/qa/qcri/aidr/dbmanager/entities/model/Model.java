// default package
// Generated Nov 24, 2014 4:55:08 PM by Hibernate Tools 4.0.0
package qa.qcri.aidr.dbmanager.entities.model;


import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.Hibernate;
import org.hibernate.collection.internal.PersistentList;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * Model generated by hbm2java
 */
@Entity
@Table(name = "model", catalog = "aidr_predict")
public class Model implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -258497973511763596L;
    private Long modelId;
    private double avgPrecision;
    private double avgRecall;
    private double avgAuc;
    private int trainingCount;
    private Date trainingTime;
    private boolean isCurrentModel;
    private List<ModelNominalLabel> modelNominalLabels = null;
    private ModelFamily modelFamily;

    public Model() {
    }

    public Model(ModelFamily modelFamily, double avgPrecision, double avgRecall,
            double avgAuc, int trainingCount, Date trainingTime,
            boolean isCurrentModel) {
        this.setModelFamily(modelFamily);
        this.avgPrecision = avgPrecision;
        this.avgRecall = avgRecall;
        this.avgAuc = avgAuc;
        this.trainingCount = trainingCount;
        this.trainingTime = trainingTime;
        this.isCurrentModel = isCurrentModel;
    }

    public Model(Long modelFamilyId, double avgPrecision, double avgRecall,
            double avgAuc, int trainingCount, Date trainingTime,
            boolean isCurrentModel, List<ModelNominalLabel> modelNominalLabels) {
    	this.setModelFamily(modelFamily);
        this.avgPrecision = avgPrecision;
        this.avgRecall = avgRecall;
        this.avgAuc = avgAuc;
        this.trainingCount = trainingCount;
        this.trainingTime = trainingTime;
        this.isCurrentModel = isCurrentModel;
        this.setModelNominalLabels(modelNominalLabels);
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "modelID", unique = true, nullable = false)
    public Long getModelId() {
        return this.modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    @Column(name = "avgPrecision", nullable = false, precision = 22, scale = 0)
    public double getAvgPrecision() {
        return this.avgPrecision;
    }

    public void setAvgPrecision(double avgPrecision) {
        this.avgPrecision = avgPrecision;
    }

    @Column(name = "avgRecall", nullable = false, precision = 22, scale = 0)
    public double getAvgRecall() {
        return this.avgRecall;
    }

    public void setAvgRecall(double avgRecall) {
        this.avgRecall = avgRecall;
    }

    @Column(name = "avgAuc", nullable = false, precision = 22, scale = 0)
    public double getAvgAuc() {
        return this.avgAuc;
    }

    public void setAvgAuc(double avgAuc) {
        this.avgAuc = avgAuc;
    }

    @Column(name = "trainingCount", nullable = false)
    public int getTrainingCount() {
        return this.trainingCount;
    }

    public void setTrainingCount(int trainingCount) {
        this.trainingCount = trainingCount;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "trainingTime", nullable = false, length = 19)
    public Date getTrainingTime() {
        return this.trainingTime;
    }

    public void setTrainingTime(Date trainingTime) {
        this.trainingTime = trainingTime;
    }

    @Column(name = "isCurrentModel", nullable = false)
    public boolean isIsCurrentModel() {
        return this.isCurrentModel;
    }

    public void setIsCurrentModel(boolean isCurrentModel) {
        this.isCurrentModel = isCurrentModel;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "model")
    @JsonManagedReference
    public List<ModelNominalLabel> getModelNominalLabels() {
        return this.modelNominalLabels;
    }

    public void setModelNominalLabels(List<ModelNominalLabel> modelNominalLabels) {
        this.modelNominalLabels = modelNominalLabels;
    }

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "modelFamilyID", referencedColumnName = "modelFamilyID", nullable = false)
	@JsonBackReference
	public ModelFamily getModelFamily() {
		return this.modelFamily;
	}

	public void setModelFamily(ModelFamily modelFamily) {
		this.modelFamily = modelFamily;
	}
    
	public boolean hasModelNominalLabel() {
		return Hibernate.isInitialized(this.modelNominalLabels);
	}
	
	public boolean hasModelFamily() {
		return Hibernate.isInitialized(modelFamily);
	}
}
