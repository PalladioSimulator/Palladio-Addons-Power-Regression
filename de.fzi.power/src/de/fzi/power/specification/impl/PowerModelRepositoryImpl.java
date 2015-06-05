/**
 */
package de.fzi.power.specification.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import de.fzi.power.specification.PowerModelRepository;
import de.fzi.power.specification.PowerModelSpecification;
import de.fzi.power.specification.SpecificationPackage;
import de.uka.ipd.sdq.identifier.impl.IdentifierImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Power Model Repository</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link de.fzi.power.specification.impl.PowerModelRepositoryImpl#getPowerModelSpecifications
 * <em>Power Model Specifications</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PowerModelRepositoryImpl extends IdentifierImpl implements PowerModelRepository {
    /**
     * The cached value of the '{@link #getPowerModelSpecifications()
     * <em>Power Model Specifications</em>}' containment reference list. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getPowerModelSpecifications()
     * @generated
     * @ordered
     */
    protected EList<PowerModelSpecification> powerModelSpecifications;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected PowerModelRepositoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return SpecificationPackage.Literals.POWER_MODEL_REPOSITORY;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EList<PowerModelSpecification> getPowerModelSpecifications() {
        if (this.powerModelSpecifications == null)
        {
            this.powerModelSpecifications = new EObjectContainmentWithInverseEList<PowerModelSpecification>(
                    PowerModelSpecification.class, this,
                    SpecificationPackage.POWER_MODEL_REPOSITORY__POWER_MODEL_SPECIFICATIONS,
                    SpecificationPackage.POWER_MODEL_SPECIFICATION__POWERMODELREPOSITORY);
        }
        return this.powerModelSpecifications;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public NotificationChain eInverseAdd(final InternalEObject otherEnd, final int featureID,
            final NotificationChain msgs) {
        switch (featureID)
        {
        case SpecificationPackage.POWER_MODEL_REPOSITORY__POWER_MODEL_SPECIFICATIONS:
            return ((InternalEList<InternalEObject>) (InternalEList<?>) this.getPowerModelSpecifications()).basicAdd(
                    otherEnd, msgs);
        }
        return super.eInverseAdd(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(final InternalEObject otherEnd, final int featureID,
            final NotificationChain msgs) {
        switch (featureID)
        {
        case SpecificationPackage.POWER_MODEL_REPOSITORY__POWER_MODEL_SPECIFICATIONS:
            return ((InternalEList<?>) this.getPowerModelSpecifications()).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object eGet(final int featureID, final boolean resolve, final boolean coreType) {
        switch (featureID)
        {
        case SpecificationPackage.POWER_MODEL_REPOSITORY__POWER_MODEL_SPECIFICATIONS:
            return this.getPowerModelSpecifications();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(final int featureID, final Object newValue) {
        switch (featureID)
        {
        case SpecificationPackage.POWER_MODEL_REPOSITORY__POWER_MODEL_SPECIFICATIONS:
            this.getPowerModelSpecifications().clear();
            this.getPowerModelSpecifications().addAll((Collection<? extends PowerModelSpecification>) newValue);
            return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eUnset(final int featureID) {
        switch (featureID)
        {
        case SpecificationPackage.POWER_MODEL_REPOSITORY__POWER_MODEL_SPECIFICATIONS:
            this.getPowerModelSpecifications().clear();
            return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean eIsSet(final int featureID) {
        switch (featureID)
        {
        case SpecificationPackage.POWER_MODEL_REPOSITORY__POWER_MODEL_SPECIFICATIONS:
            return this.powerModelSpecifications != null && !this.powerModelSpecifications.isEmpty();
        }
        return super.eIsSet(featureID);
    }

} // PowerModelRepositoryImpl
