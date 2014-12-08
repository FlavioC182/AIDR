// default package
// Generated Nov 24, 2014 4:55:08 PM by Hibernate Tools 4.0.0
package qa.qcri.aidr.dbmanager.entities.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.Hibernate;

import com.fasterxml.jackson.annotation.JsonBackReference;


/**
 * ModelNominalLabel generated by hbm2java
 */
@Entity
@Table(name = "model_nominal_label", catalog = "aidr_predict")
public class ModelNominalLabel implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1454244707175228621L;
	private ModelNominalLabelId id;
	private NominalLabel nominalLabel;
	private Model model;
	private Double labelPrecision;
	private Double labelRecall;
	private Double labelAuc;
	private Integer classifiedDocumentCount;

	public ModelNominalLabel() {
	}

	public ModelNominalLabel(ModelNominalLabelId id, NominalLabel nominalLabel,
			Model model) {
		this.id = id;
		this.nominalLabel = nominalLabel;
		this.model = model;
	}

	public ModelNominalLabel(ModelNominalLabelId id, NominalLabel nominalLabel,
			Model model, Double labelPrecision, Double labelRecall,
			Double labelAuc, Integer classifiedDocumentCount) {
		this.id = id;
		this.nominalLabel = nominalLabel;
		this.model = model;
		this.labelPrecision = labelPrecision;
		this.labelRecall = labelRecall;
		this.labelAuc = labelAuc;
		this.classifiedDocumentCount = classifiedDocumentCount;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "modelId", column = @Column(name = "modelID", nullable = false)),
			@AttributeOverride(name = "nominalLabelId", column = @Column(name = "nominalLabelID", nullable = false)) })
	public ModelNominalLabelId getId() {
		return this.id;
	}

	public void setId(ModelNominalLabelId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "nominalLabelID", nullable = false, insertable = false, updatable = false)
	@JsonBackReference
	public NominalLabel getNominalLabel() {
		return this.nominalLabel;
	}

	public void setNominalLabel(NominalLabel nominalLabel) {
		this.nominalLabel = nominalLabel;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "modelID", nullable = false, insertable = false, updatable = false)
	@JsonBackReference
	public Model getModel() {
		return this.model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	@Column(name = "labelPrecision", precision = 22, scale = 0)
	public Double getLabelPrecision() {
		return this.labelPrecision;
	}

	public void setLabelPrecision(Double labelPrecision) {
		this.labelPrecision = labelPrecision;
	}

	@Column(name = "labelRecall", precision = 22, scale = 0)
	public Double getLabelRecall() {
		return this.labelRecall;
	}

	public void setLabelRecall(Double labelRecall) {
		this.labelRecall = labelRecall;
	}

	@Column(name = "labelAuc", precision = 22, scale = 0)
	public Double getLabelAuc() {
		return this.labelAuc;
	}

	public void setLabelAuc(Double labelAuc) {
		this.labelAuc = labelAuc;
	}

	@Column(name = "classifiedDocumentCount")
	public Integer getClassifiedDocumentCount() {
		return this.classifiedDocumentCount;
	}

	public void setClassifiedDocumentCount(Integer classifiedDocumentCount) {
		this.classifiedDocumentCount = classifiedDocumentCount;
	}
	
	public boolean hasNominalLabel() {
		return Hibernate.isInitialized(this.nominalLabel);
	}
	
	public boolean hasModel() {
		return Hibernate.isInitialized(this.model);
	}
}
