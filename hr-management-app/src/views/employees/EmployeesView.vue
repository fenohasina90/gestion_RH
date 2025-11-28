<template>
    <div class="employees-container">
        <div class="page-header">
            <div class="header-left">
                <h1>Gestion des Employés</h1>
                <p>Liste et filtrage des employés de l'entreprise</p>
            </div>
            <div class="header-actions">
                <button class="btn btn-primary" @click="$router.push('/employees/add')">
                    + Ajouter un employé
                </button>
            </div>
        </div>

        <!-- Filtres -->
        <div class="filters-section">
            <div class="filters-grid">
                <!-- Recherche par nom -->
                <div class="filter-group">
                    <label>Nom/Prénom</label>
                    <input
                            type="text"
                            v-model="filters.nom"
                            placeholder="Rechercher par nom ou prénom..."
                            class="filter-input"
                    />
                </div>

                <!-- Filtre par département -->
                <div class="filter-group">
                    <label>Département</label>
                    <select v-model="filters.departementId" class="filter-select">
                        <option value="">Tous les départements</option>
                        <option v-for="dept in departements" :key="dept.id" :value="dept.id">
                            {{ dept.nom }}
                        </option>
                    </select>
                </div>

                <!-- Filtre par poste -->
                <div class="filter-group">
                    <label>Poste</label>
                    <input
                            type="text"
                            v-model="filters.poste"
                            placeholder="Rechercher par poste..."
                            class="filter-input"
                    />
                </div>

                <!-- Filtre par type de contrat -->
                <div class="filter-group">
                    <label>Type de contrat</label>
                    <select v-model="filters.typeContrat" class="filter-select">
                        <option value="">Tous les types</option>
                        <option value="CDI">CDI</option>
                        <option value="CDD">CDD</option>
                        <option value="Stage">Stage</option>
                        <option value="Freelance">Freelance</option>
                    </select>
                </div>

                <!-- Filtre par salaire -->
                <div class="filter-group">
                    <label>Salaire minimum</label>
                    <input
                            type="number"
                            v-model="filters.salaireMin"
                            placeholder="Salaire min..."
                            class="filter-input"
                    />
                </div>

                <!-- Filtre par date de début -->
                <div class="filter-group">
                    <label>Contrat depuis</label>
                    <input
                            type="date"
                            v-model="filters.dateDebutFrom"
                            class="filter-input"
                    />
                </div>
            </div>

            <div class="filters-actions">
                <button @click="clearFilters" class="btn btn-secondary">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M3 6h18"></path>
                        <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"></path>
                        <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"></path>
                    </svg>
                    Effacer les filtres
                </button>
                <span class="results-count">{{ filteredEmployees.length }} employé(s) trouvé(s)</span>
            </div>
        </div>

        <!-- Liste des employés -->
        <div class="employees-grid" v-if="filteredEmployees.length > 0">
            <div
                    v-for="employee in paginatedEmployees"
                    :key="employee.id"
                    class="employee-card"
            >
                <div class="employee-header">
                    <div class="employee-avatar">
                        <span>{{ getInitials(employee.idemploye?.nom, employee.idemploye?.prenom) }}</span>
                    </div>
                    <div class="employee-info">
                        <h3>{{ employee.idemploye?.prenom }} {{ employee.idemploye?.nom }}</h3>
                        <p class="employee-poste">{{ employee.poste || 'Poste non défini' }}</p>
                    </div>
                    <div class="employee-status" :class="getStatusClass(employee.typecontrat)">
                        {{ employee.typecontrat || 'Non défini' }}
                    </div>
                </div>

                <div class="employee-details">
                    <div class="detail-item">
                        <span class="detail-label">Département:</span>
                        <span class="detail-value">{{ employee.idemploye?.iddept?.nom || 'Non assigné' }}</span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Adresse:</span>
                        <span class="detail-value">{{ employee.idemploye?.adresse || 'Non renseignée' }}</span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Type contrat:</span>
                        <span class="detail-value">{{ employee.typecontrat || 'Non renseigné' }}</span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Salaire:</span>
                        <span class="detail-value">{{ formatSalaire(employee.salaire) }}</span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Date début:</span>
                        <span class="detail-value">{{ formatDate(employee.datedebut) }}</span>
                    </div>
                </div>

                <div class="employee-actions">
                    <router-link :to="`/employees/contract/${employee.id}`" class="btn btn-sm btn-primary">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                            <circle cx="12" cy="12" r="3"></circle>
                        </svg>
                        Voir
                    </router-link>
                    <button @click="openHistory(employee)" class="btn btn-sm btn-success">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M3 3v5h5"></path>
                            <path d="M3 13a9 9 0 1 0 3-7.7L3 8"></path>
                        </svg>
                        Historique
                    </button>
                    <router-link :to="`/employees/${employee.idemploye?.id}/documents`" class="btn btn-sm btn-info">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                            <polyline points="14,2 14,8 20,8"></polyline>
                        </svg>
                        Documents
                    </router-link>
                    <button @click="editEmployee(employee)" class="btn btn-sm btn-secondary">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                        </svg>
                        Modifier
                    </button>
                </div>
            </div>
        </div>

        <!-- Message si aucun employé -->
        <div v-else class="no-results">
            <div class="no-results-icon">
                <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1">
                    <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"></path>
                    <circle cx="9" cy="7" r="4"></circle>
                    <path d="m22 21-3-3m0 0a5.5 5.5 0 1 0-7.78-7.78 5.5 5.5 0 0 0 7.78 7.78Z"></path>
                </svg>
            </div>
            <h3>Aucun employé trouvé</h3>
            <p>Aucun employé ne correspond aux critères de recherche sélectionnés.</p>
        </div>

        <!-- Pagination -->
        <div class="pagination" v-if="totalPages > 1">
            <button
                    @click="currentPage = Math.max(1, currentPage - 1)"
                    :disabled="currentPage === 1"
                    class="btn btn-secondary"
            >
                Précédent
            </button>
            <span class="pagination-info">
        Page {{ currentPage }} sur {{ totalPages }}
      </span>
            <button
                    @click="currentPage = Math.min(totalPages, currentPage + 1)"
                    :disabled="currentPage === totalPages"
                    class="btn btn-secondary"
            >
                Suivant
            </button>
        </div>

        <!-- Loading -->
        <div v-if="isLoading" class="loading">
            <div class="loading-spinner"></div>
            <p>Chargement des employés...</p>
        </div>

        <!-- Modal détails employé -->
        <div v-if="showEmployeeModal" class="modal-overlay" @click="closeEmployeeModal">
            <div class="modal-content employee-modal" @click.stop>
                <div class="modal-header">
                    <h2>Détails de l'employé</h2>
                    <button @click="closeEmployeeModal" class="modal-close">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <line x1="18" y1="6" x2="6" y2="18"></line>
                            <line x1="6" y1="6" x2="18" y2="18"></line>
                        </svg>
                    </button>
                </div>

                <div class="modal-body" v-if="selectedEmployee">
                    <!-- Onglets de navigation -->
                    <div class="section-tabs">
                        <button 
                            @click="activeSection = 'personal'" 
                            :class="['tab-button', { active: activeSection === 'personal' }]"
                        >
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                                <circle cx="12" cy="7" r="4"></circle>
                            </svg>
                            Informations personnelles
                        </button>
                        <button 
                            @click="activeSection = 'contract'" 
                            :class="['tab-button', { active: activeSection === 'contract' }]"
                        >
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                                <polyline points="14,2 14,8 20,8"></polyline>
                                <line x1="16" y1="13" x2="8" y2="13"></line>
                                <line x1="16" y1="17" x2="8" y2="17"></line>
                                <polyline points="10,9 9,9 8,9"></polyline>
                            </svg>
                            Contrat
                        </button>
                        <button 
                            @click="activeSection = 'status'" 
                            :class="['tab-button', { active: activeSection === 'status' }]"
                        >
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <polyline points="22,12 18,12 15,21 9,3 6,12 2,12"></polyline>
                            </svg>
                            Statut
                        </button>
                    </div>

                    <!-- Contenu des sections -->
                    <div class="section-content">
                        <!-- Informations personnelles -->
                        <div v-if="activeSection === 'personal'" class="info-table">
                            <!-- Photo + identité -->
                            <div class="table-row full-width" style="align-items: center;">
                                <div class="table-label">Photo</div>
                                <div class="table-value" style="display:flex; align-items:center; gap:16px;">
                                    <div class="employee-avatar" style="width:64px;height:64px;border-radius:12px; overflow:hidden; display:flex;align-items:center;justify-content:center; background:#f3f4f6;">
                                        <img v-if="selectedEmployee.idemploye?.photo" :src="getPhotoUrl(selectedEmployee.idemploye.photo)" alt="Photo employé" style="width:100%;height:100%;object-fit:cover;" />
                                        <span v-else>{{ getInitials(selectedEmployee.idemploye?.nom, selectedEmployee.idemploye?.prenom) }}</span>
                                    </div>
                                    <div>
                                        <div style="font-weight:600;">{{ selectedEmployee.idemploye?.prenom }} {{ selectedEmployee.idemploye?.nom }}</div>
                                        <div style="color:#6b7280;">{{ selectedEmployee.idemploye?.iddept?.nom || 'Département non assigné' }}</div>
                                    </div>
                                </div>
                            </div>

                            <div class="table-row">
                                <div class="table-label">Adresse</div>
                                <div class="table-value">{{ selectedEmployee.idemploye?.adresse || 'Non renseignée' }}</div>
                            </div>
                            <div class="table-row">
                                <div class="table-label">Email</div>
                                <div class="table-value">{{ selectedEmployee.idemploye?.email || 'Non renseigné' }}</div>
                            </div>
                            <div class="table-row">
                                <div class="table-label">Téléphone</div>
                                <div class="table-value">{{ selectedEmployee.idemploye?.telephone || 'Non renseigné' }}</div>
                            </div>
                            <div class="table-row">
                                <div class="table-label">Date de naissance</div>
                                <div class="table-value">{{ formatDate(selectedEmployee.idemploye?.datenaissance) }}</div>
                            </div>
                            <div class="table-row">
                                <div class="table-label">Lieu de naissance</div>
                                <div class="table-value">{{ selectedEmployee.idemploye?.lieunaissance || 'Non renseigné' }}</div>
                            </div>
                            <div class="table-row">
                                <div class="table-label">Nationalité</div>
                                <div class="table-value">{{ selectedEmployee.idemploye?.nationalite || 'Non renseignée' }}</div>
                            </div>
                            <div class="table-row">
                                <div class="table-label">Situation familiale</div>
                                <div class="table-value">{{ selectedEmployee.idemploye?.situationfamiliale || 'Non renseignée' }}</div>
                            </div>
                            <div class="table-row">
                                <div class="table-label">Nombre d'enfants</div>
                                <div class="table-value">{{ selectedEmployee.idemploye?.nombreenfants ?? '0' }}</div>
                            </div>
                            <div class="table-row">
                                <div class="table-label">Numéro CNAPS</div>
                                <div class="table-value">{{ selectedEmployee.idemploye?.numerocnaps || 'Non renseigné' }}</div>
                            </div>
                            <div class="table-row">
                                <div class="table-label">Numéro OSTIE</div>
                                <div class="table-value">{{ selectedEmployee.idemploye?.numeroostie || 'Non renseigné' }}</div>
                            </div>
                            <div class="table-row">
                                <div class="table-label">Matricule</div>
                                <div class="table-value">{{ selectedEmployee.idemploye?.matricule || 'Non renseigné' }}</div>
                            </div>
                        </div>

                        <!-- Informations du contrat -->
                        <div v-if="activeSection === 'contract'" class="info-table">
                            <div class="table-row">
                                <div class="table-label">Poste</div>
                                <div class="table-value">{{ selectedEmployee.poste || 'Non défini' }}</div>
                            </div>
                            <div class="table-row">
                                <div class="table-label">Type de contrat</div>
                                <div class="table-value">
                                    <span class="contract-type" :class="getContractTypeClass(selectedEmployee.typecontrat)">
                                        {{ selectedEmployee.typecontrat || 'Non défini' }}
                                    </span>
                                </div>
                            </div>
                            <div class="table-row">
                                <div class="table-label">Salaire</div>
                                <div class="table-value salary">{{ formatSalaire(selectedEmployee.salaire) }}</div>
                            </div>
                            <div class="table-row">
                                <div class="table-label">Date de début</div>
                                <div class="table-value">{{ formatDate(selectedEmployee.datedebut) }}</div>
                            </div>
                            <div class="table-row">
                                <div class="table-label">Durée (mois)</div>
                                <div class="table-value">{{ selectedEmployee.nombremois || 'Indéterminée' }}</div>
                            </div>
                            <div class="table-row">
                                <div class="table-label">Date de fin</div>
                                <div class="table-value">{{ getContractEndDate(selectedEmployee) }}</div>
                            </div>
                        </div>

                        <!-- Statut du contrat -->
                        <div v-if="activeSection === 'status'" class="info-table">
                            <div class="table-row">
                                <div class="table-label">Statut actuel</div>
                                <div class="table-value">
                                    <span class="status-badge" :class="getContractStatusClass(selectedEmployee)">
                                        {{ getContractStatus(selectedEmployee) }}
                                    </span>
                                </div>
                            </div>
                            <div class="table-row full-width">
                                <div class="table-label">Description</div>
                                <div class="table-value description">{{ getContractStatusDescription(selectedEmployee) }}</div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="modal-footer">
                    <button @click="editEmployee(selectedEmployee)" class="btn btn-primary">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                        </svg>
                        Modifier
                    </button>
                    <button @click="closeEmployeeModal" class="btn btn-secondary">Fermer</button>
                </div>
            </div>
        </div>
        
        <!-- Modal Historique postes -->
        <div v-if="showHistoryModal" class="modal-overlay history" @click="closeHistoryModal">
            <div class="modal-content history" @click.stop>
                <div class="modal-header history">
                    <h2>Historique des postes, promotions, mobilités</h2>
                    <button class="btn btn-secondary" @click="closeHistoryModal">Fermer</button>
                </div>
                <div class="modal-body history">
                    <div v-if="isHistoryLoading">Chargement...</div>
                    <div v-else>
                        <table class="history-table" v-if="historyItems.length">
                            <thead>
                                <tr>
                                    <th>Période</th>
                                    <th>Poste occupé</th>
                                    <th>Département</th>
                                    <th>Catégorie</th>
                                    <th>Motif</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="h in historyItems" :key="h.id">
                                    <td>{{ formatDate(h.datedebut) }} → {{ h.datefin ? formatDate(h.datefin) : 'En cours' }}</td>
                                    <td>
                                        {{ h.posteoccupe || '—' }}
                                    </td>
                                    <td>{{ h.iddepartement?.nom || '—' }}</td>
                                    <td>{{ h.idcategorie?.nom || '—' }}</td>
                                    <td>
                                        <span v-if="h.motif" class="badge badge-promo" v-text="h.motif"></span>
                                        <span v-else class="badge badge-move">—</span>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                        <div v-else>Aucun historique trouvé pour cet employé.</div>
                    </div>
                </div>
                <div class="modal-footer history">
                    <button class="btn btn-secondary" @click="closeHistoryModal">Fermer</button>
                </div>
            </div>
        </div>

        <!-- Modal Documents -->
        <div v-if="showDocumentsModal" class="modal-overlay history" @click="closeDocumentsModal">
            <div class="modal-content documents" @click.stop>
                <div class="modal-header history">
                    <h2>Documents RH (CIN, diplômes, attestations, etc.)</h2>
                    <button class="btn btn-secondary" @click="closeDocumentsModal">Fermer</button>
                </div>
                <div class="modal-body documents">
                    <div class="docs-list">
                        <table>
                            <thead>
                                <tr>
                                    <th>Type</th>
                                    <th>Nom fichier</th>
                                    <th>Date upload</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-if="isDocumentsLoading"><td colspan="4">Chargement...</td></tr>
                                <tr v-else-if="!documentsItems.length"><td colspan="4">Aucun document</td></tr>
                                <tr v-else v-for="d in documentsItems" :key="d.id">
                                    <td>{{ d.idtypedocument?.nom || '—' }}</td>
                                    <td>{{ d.nomfichier }}</td>
                                    <td>{{ d.dateupload ? new Date(d.dateupload).toLocaleString('fr-FR') : '—' }}</td>
                                    <td>
                                        <button class="btn btn-sm btn-primary" @click.stop="viewDocumentDetails(d)">Détails</button>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                    <div class="doc-details">
                        <h3 style="margin:0 0 8px 0;">Détails du document</h3>
                        <div v-if="!selectedDocument">Sélectionnez un document pour voir les détails.</div>
                        <div v-else>
                            <div style="margin-bottom:8px; color:#6b7280;">{{ selectedDocument.nomfichier }}</div>
                            <div class="json-tree" v-if="selectedDocumentParsed">
                                <div v-for="(val, key) in selectedDocumentParsed" :key="key" class="json-pair">
                                    <div><span class="json-key">{{ key }}</span>:
                                        <span v-if="typeof val !== 'object'" class="json-value">{{ val }}</span>
                                    </div>
                                    <div v-if="typeof val === 'object'" class="json-indent">
                                        <div v-for="(subVal, subKey) in val" :key="subKey" class="json-pair">
                                            <span class="json-key">{{ subKey }}</span>:
                                            <span v-if="typeof subVal !== 'object'" class="json-value">{{ subVal }}</span>
                                            <div v-else class="json-indent">
                                                <div v-for="(s2, k2) in subVal" :key="k2" class="json-pair">
                                                    <span class="json-key">{{ k2 }}</span>: <span class="json-value">{{ s2 }}</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div v-else>Aucun contenu JSON à afficher.</div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer history">
                    <button class="btn btn-secondary" @click="closeDocumentsModal">Fermer</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import axios from 'axios'

export default {
    name: 'EmployeesView',
    data() {
        return {
            employees: [],
            departements: [],
            isLoading: false,
            currentPage: 1,
            itemsPerPage: 12,
            filters: {
                nom: '',
                departementId: '',
                poste: '',
                typeContrat: '',
                salaireMin: '',
                dateDebutFrom: ''
            },
            debounceTimer: null,
            selectedEmployee: null,
            showEmployeeModal: false,
            activeSection: 'personal',
            // Historique postes modal
            showHistoryModal: false,
            historyEmployee: null,
            historyItems: [],
            isHistoryLoading: false,
            // Documents modal
            showDocumentsModal: false,
            documentsEmployee: null,
            documentsItems: [],
            isDocumentsLoading: false,
            selectedDocument: null,
            selectedDocumentParsed: null
        }
    },
    computed: {
        uniqueEmployees() {
            // Conserver un seul contrat (le plus récent) par employé
            const byEmp = new Map()
            for (const c of this.employees) {
                const empId = c?.idemploye?.id
                if (!empId) continue
                const existing = byEmp.get(empId)
                if (!existing) {
                    byEmp.set(empId, c)
                } else {
                    const d1 = existing?.datedebut ? new Date(existing.datedebut) : null
                    const d2 = c?.datedebut ? new Date(c.datedebut) : null
                    // Garder le plus récent (date début la plus récente)
                    if (d2 && (!d1 || d2 > d1)) {
                        byEmp.set(empId, c)
                    }
                }
            }
            return Array.from(byEmp.values())
        },
        filteredEmployees() {
            let filtered = [...this.uniqueEmployees]

            // Filtre par nom/prénom
            if (this.filters.nom) {
                const searchTerm = this.filters.nom.toLowerCase()
                filtered = filtered.filter(emp =>
                    (emp.idemploye && emp.idemploye.nom && emp.idemploye.nom.toLowerCase().includes(searchTerm)) ||
                    (emp.idemploye && emp.idemploye.prenom && emp.idemploye.prenom.toLowerCase().includes(searchTerm))
                )
            }

            // Filtre par département
            if (this.filters.departementId) {
                filtered = filtered.filter(emp =>
                    emp.idemploye && emp.idemploye.iddept && String(emp.idemploye.iddept.id) === String(this.filters.departementId)
                )
            }

            // Filtre par poste
            if (this.filters.poste) {
                const posteTerm = this.filters.poste.toLowerCase()
                filtered = filtered.filter(emp =>
                    emp.poste && emp.poste.toLowerCase().includes(posteTerm)
                )
            }

            // Filtre par type de contrat
            if (this.filters.typeContrat) {
                filtered = filtered.filter(emp => emp.typecontrat === this.filters.typeContrat)
            }

            // Filtre par salaire minimum
            if (this.filters.salaireMin) {
                filtered = filtered.filter(emp =>
                    emp.salaire && emp.salaire >= parseFloat(this.filters.salaireMin)
                )
            }

            // Filtre par date de début
            if (this.filters.dateDebutFrom) {
                const fromDate = new Date(this.filters.dateDebutFrom)
                filtered = filtered.filter(emp =>
                    emp.datedebut && new Date(emp.datedebut) >= fromDate
                )
            }

            return filtered
        },
        paginatedEmployees() {
            const start = (this.currentPage - 1) * this.itemsPerPage
            const end = start + this.itemsPerPage
            return this.filteredEmployees.slice(start, end)
        },
        totalPages() {
            return Math.ceil(this.filteredEmployees.length / this.itemsPerPage)
        }
    },
    watch: {
        filters: {
            handler() {
                this.currentPage = 1
                this.debouncedFilter()
            },
            deep: true
        }
    },
    async mounted() {
        await this.loadEmployees()
        await this.loadDepartements()
    },
    methods: {
        async loadEmployees() {
            this.isLoading = true
            try {
                const response = await axios.get('/api/employes')
                this.employees = Array.isArray(response.data) ? response.data : []
            } catch (error) {
                console.error('Erreur lors du chargement des employés:', error)
                this.employees = []
            } finally {
                this.isLoading = false
            }
        },
        async loadDepartements() {
            try {
                const response = await axios.get('/api/departements')
                this.departements = Array.isArray(response.data) ? response.data : []
            } catch (error) {
                console.error('Erreur lors du chargement des départements:', error)
                this.departements = []
            }
        },
        debouncedFilter() {
            clearTimeout(this.debounceTimer)
            this.debounceTimer = setTimeout(() => {
                // Ici on pourrait appeler une API de filtrage côté serveur si nécessaire
            }, 300)
        },
        clearFilters() {
            this.filters = {
                nom: '',
                departementId: '',
                poste: '',
                typeContrat: '',
                salaireMin: '',
                dateDebutFrom: ''
            }
            this.currentPage = 1
        },
        getInitials(nom, prenom) {
            const n = nom ? nom.charAt(0).toUpperCase() : ''
            const p = prenom ? prenom.charAt(0).toUpperCase() : ''
            return n + p || '??'
        },
        getStatusClass(statut) {
            switch(statut) {
                case 'actif': return 'status-active'
                case 'inactif': return 'status-inactive'
                case 'conge': return 'status-leave'
                default: return 'status-unknown'
            }
        },
        getStatusLabel(statut) {
            switch(statut) {
                case 'actif': return 'Actif'
                case 'inactif': return 'Inactif'
                case 'conge': return 'En congé'
                default: return 'Non défini'
            }
        },
        formatSalaire(salaire) {
            if (!salaire) return 'Non renseigné'
            return new Intl.NumberFormat('fr-FR', {
                style: 'currency',
                currency: 'EUR'
            }).format(salaire)
        },
        formatDate(date) {
            if (!date) return 'Non renseignée'
            return new Date(date).toLocaleDateString('fr-FR')
        },
        viewEmployee(employee) {
            this.selectedEmployee = employee
            this.showEmployeeModal = true
        },
        editEmployee(employee) {
            // TODO: Implémenter l'édition
            console.log('Modifier employé:', employee)
        },
        closeEmployeeModal() {
            this.showEmployeeModal = false
            this.selectedEmployee = null
            this.activeSection = 'personal'
        },
        async openHistory(employee) {
            this.historyEmployee = employee
            this.showHistoryModal = true
            this.isHistoryLoading = true
            this.historyItems = []
            try {
                const empId = employee?.idemploye?.id
                if (!empId) return
                const res = await axios.get(`/api/employes/${empId}/historiquepostes`)
                this.historyItems = Array.isArray(res.data) ? res.data : []
            } catch (e) {
                console.error('Erreur chargement historique postes', e)
                this.historyItems = []
            } finally {
                this.isHistoryLoading = false
            }
        },
        closeHistoryModal() {
            this.showHistoryModal = false
            this.historyEmployee = null
            this.historyItems = []
        },
        async openDocuments(employee) {
            this.documentsEmployee = employee
            this.showDocumentsModal = true
            this.isDocumentsLoading = true
            this.documentsItems = []
            this.selectedDocument = null
            this.selectedDocumentParsed = null
            try {
                const empId = employee?.idemploye?.id
                if (!empId) return
                const res = await axios.get(`/api/employes/${empId}/documents`)
                this.documentsItems = Array.isArray(res.data) ? res.data : []
            } catch (e) {
                console.error('Erreur chargement documents employé', e)
                this.documentsItems = []
            } finally {
                this.isDocumentsLoading = false
            }
        },
        closeDocumentsModal() {
            this.showDocumentsModal = false
            this.documentsEmployee = null
            this.documentsItems = []
            this.selectedDocument = null
            this.selectedDocumentParsed = null
        },
        viewDocumentDetails(doc) {
            this.selectedDocument = doc
            this.selectedDocumentParsed = this.parseJson(doc?.contenujson)
        },
        parseJson(text) {
            if (!text) return null
            try { return JSON.parse(text) } catch { return null }
        },
        getPhotoUrl(path) {
            if (!path) return null
            // Absolute URL already
            if (/^https?:\/\//i.test(path)) return path
            // Ensure leading slash
            const normalized = path.startsWith('/') ? path : `/${path}`
            const base = import.meta.env.VITE_API_BASE_URL || ''
            return `${base}${normalized}`
        },
        getContractTypeClass(type) {
            if (!type) return ''
            switch (type.toLowerCase()) {
                case 'cdi': return 'type-cdi'
                case 'cdd': return 'type-cdd'
                case 'stage': return 'type-stage'
                case 'freelance': return 'type-freelance'
                default: return 'type-other'
            }
        },
        getContractEndDate(employee) {
            if (!employee.datedebut || !employee.nombremois) {
                return 'Non définie'
            }
            const startDate = new Date(employee.datedebut)
            const endDate = new Date(startDate)
            endDate.setMonth(endDate.getMonth() + parseInt(employee.nombremois))
            return this.formatDate(endDate)
        },
        getContractStatus(employee) {
            if (!employee.datedebut) return 'Statut inconnu'
            
            const today = new Date()
            const startDate = new Date(employee.datedebut)
            
            if (startDate > today) {
                return 'À venir'
            }
            
            if (employee.nombremois) {
                const endDate = new Date(startDate)
                endDate.setMonth(endDate.getMonth() + parseInt(employee.nombremois))
                
                if (today > endDate) {
                    return 'Expiré'
                } else {
                    return 'Actif'
                }
            }
            
            return 'Actif'
        },
        getContractStatusClass(employee) {
            const status = this.getContractStatus(employee)
            switch (status) {
                case 'Actif': return 'status-active'
                case 'À venir': return 'status-upcoming'
                case 'Expiré': return 'status-expired'
                default: return 'status-unknown'
            }
        },
        getContractStatusDescription(employee) {
            const status = this.getContractStatus(employee)
            
            switch (status) {
                case 'Actif':
                    if (employee.nombremois) {
                        const startDate = new Date(employee.datedebut)
                        const endDate = new Date(startDate)
                        endDate.setMonth(endDate.getMonth() + parseInt(employee.nombremois))
                        const daysLeft = Math.ceil((endDate - new Date()) / (1000 * 60 * 60 * 24))
                        return `Contrat actif. ${daysLeft > 0 ? `${daysLeft} jours restants.` : 'Expire bientôt.'}`
                    }
                    return 'Contrat à durée indéterminée en cours.'
                case 'À venir':
                    const daysUntilStart = Math.ceil((new Date(employee.datedebut) - new Date()) / (1000 * 60 * 60 * 24))
                    return `Le contrat commencera dans ${daysUntilStart} jours.`
                case 'Expiré':
                    return 'Ce contrat a expiré et doit être renouvelé ou clôturé.'
                default:
                    return 'Statut du contrat non déterminé.'
            }
        }
    }
}
</script>

<style scoped>
.employees-container {
    padding: 24px;
    max-width: 1400px;
    margin: 0 auto;
}

.page-header {
    margin-bottom: 32px;
}

.page-header h1 {
    font-size: 28px;
    font-weight: 600;
    color: #1f2937;
    margin: 0 0 8px 0;
}

.page-header p {
    color: #6b7280;
    margin: 0;
}

/* Filtres */
.filters-section {
    background: white;
    border-radius: 12px;
    padding: 24px;
    margin-bottom: 24px;
    box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.filters-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
    margin-bottom: 20px;
}

.filter-group {
    display: flex;
    flex-direction: column;
    gap: 6px;
}

.filter-group label {
    font-size: 14px;
    font-weight: 500;
    color: #374151;
}

.filter-input, .filter-select {
    padding: 10px 12px;
    border: 1px solid #d1d5db;
    border-radius: 8px;
    font-size: 14px;
    transition: border-color 0.2s ease;
}

.filter-input:focus, .filter-select:focus {
    outline: none;
    border-color: #059669;
    box-shadow: 0 0 0 3px rgba(5, 150, 105, 0.1);
}

.filters-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 20px;
    border-top: 1px solid #e5e7eb;
}

.results-count {
    font-size: 14px;
    color: #6b7280;
    font-weight: 500;
}

/* Grille des employés */
.employees-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 20px;
    margin-bottom: 32px;
}

.employee-card {
    background: white;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 1px 3px rgba(0,0,0,0.1);
    transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.employee-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.employee-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 16px;
}

.employee-avatar {
    width: 48px;
    height: 48px;
    background: #059669;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-weight: 600;
    font-size: 16px;
}

.employee-info {
    flex: 1;
}

.employee-info h3 {
    font-size: 16px;
    font-weight: 600;
    color: #1f2937;
    margin: 0 0 4px 0;
}

.employee-poste {
    font-size: 14px;
    color: #6b7280;
    margin: 0;
}

.employee-status {
    padding: 4px 12px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 500;
}

.status-active {
    background: #dcfce7;
    color: #166534;
}

.status-inactive {
    background: #fee2e2;
    color: #991b1b;
}

.status-leave {
    background: #fef3c7;
    color: #92400e;
}

.status-unknown {
    background: #f3f4f6;
    color: #6b7280;
}

.employee-details {
    display: flex;
    flex-direction: column;
    gap: 8px;
    margin-bottom: 16px;
}

.detail-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.detail-label {
    font-size: 13px;
    color: #6b7280;
    font-weight: 500;
}

.detail-value {
    font-size: 13px;
    color: #1f2937;
    text-align: right;
    max-width: 60%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.employee-actions {
    display: flex;
    gap: 8px;
}

/* Messages et états */
.no-results {
    text-align: center;
    padding: 60px 20px;
    color: #6b7280;
}

.no-results-icon {
    margin-bottom: 16px;
    opacity: 0.5;
}

.no-results h3 {
    font-size: 18px;
    font-weight: 500;
    color: #374151;
    margin: 0 0 8px 0;
}

.no-results p {
    margin: 0;
}

.loading {
    text-align: center;
    padding: 60px 20px;
    color: #666;
}

.loading-spinner {
    width: 40px;
    height: 40px;
    border: 4px solid #f3f3f3;
    border-top: 4px solid #007bff;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin: 0 auto 20px;
}

@keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}

/* Modal Styles */
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.6);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
    backdrop-filter: blur(4px);
}

.modal-content {
    background: white;
    border-radius: 16px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
    max-width: 800px;
    width: 90%;
    max-height: 90vh;
    overflow-y: auto;
    animation: modalSlideIn 0.3s ease-out;
}

.employee-modal {
    max-width: 900px;
}

@keyframes modalSlideIn {
    from {
        opacity: 0;
        transform: translateY(-30px) scale(0.95);
    }
    to {
        opacity: 1;
        transform: translateY(0) scale(1);
    }
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 24px 32px;
    border-bottom: 1px solid #e9ecef;
    background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
    color: white;
    border-radius: 16px 16px 0 0;
}

.modal-header h2 {
    margin: 0;
    font-size: 1.5rem;
    font-weight: 600;
}

.modal-close {
    background: none;
    border: none;
    color: white;
    cursor: pointer;
    padding: 8px;
    border-radius: 8px;
    transition: background-color 0.2s;
}

.modal-close:hover {
    background: rgba(255, 255, 255, 0.1);
}

.modal-body {
    padding: 32px;
}

.info-section {
    margin-bottom: 32px;
}

.info-section:last-child {
    margin-bottom: 0;
}

.info-section h3 {
    color: #2c3e50;
    font-size: 1.25rem;
    font-weight: 600;
    margin-bottom: 20px;
    padding-bottom: 8px;
    border-bottom: 2px solid #e9ecef;
}

.info-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 20px;
}

.info-item {
    display: flex;
    flex-direction: column;
    gap: 4px;
}

.info-label {
    font-weight: 600;
    color: #6c757d;
    font-size: 0.9rem;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.info-value {
    font-size: 1.1rem;
    color: #2c3e50;
    font-weight: 500;
}

.info-value.salary {
    color: #28a745;
    font-weight: 600;
    font-size: 1.2rem;
}

.contract-type {
    padding: 4px 12px;
    border-radius: 20px;
    font-size: 0.9rem;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.type-cdi {
    background: #d4edda;
    color: #155724;
}

.type-cdd {
    background: #fff3cd;
    color: #856404;
}

.type-stage {
    background: #d1ecf1;
    color: #0c5460;
}

.type-freelance {
    background: #f8d7da;
    color: #721c24;
}

.type-other {
    background: #e2e3e5;
    color: #383d41;
}

.contract-status {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 20px;
    background: #f8f9fa;
    border-radius: 12px;
    border-left: 4px solid #007bff;
}

.status-badge {
    padding: 8px 16px;
    border-radius: 20px;
    font-weight: 600;
    font-size: 0.9rem;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    min-width: 80px;
    text-align: center;
}

.status-active {
    background: #d4edda;
    color: #155724;
}

.status-upcoming {
    background: #d1ecf1;
    color: #0c5460;
}

.status-expired {
    background: #f8d7da;
    color: #721c24;
}

.status-unknown {
    background: #e2e3e5;
    color: #383d41;
}

.status-details {
    flex: 1;
}

.modal-content.history {
    width: 100%;
    max-width: 900px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 10px 30px rgba(0,0,0,0.2);
    overflow: hidden;
    display: flex;
    flex-direction: column;
}

.modal-header.history {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 18px 22px;
    border-bottom: 1px solid #e5e7eb;
}

.modal-body.history {
    padding: 22px;
    max-height: 70vh;
    overflow: auto;
}

.modal-footer.history {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding: 16px 22px;
    border-top: 1px solid #e5e7eb;
}

.history-table {
    width: 100%;
    border-collapse: collapse;
}

.history-table th, .history-table td {
    padding: 12px 14px;
    border-bottom: 1px solid #eef2f7;
    text-align: left;
    font-size: 14px;
    line-height: 1.5;
}

.history-table thead th {
    background: #f9fafb;
    color: #374151;
    font-weight: 600;
}

.history-table tbody tr:hover {
    background: #fafafa;
}

.history-table tbody tr td:first-child {
    white-space: nowrap;
}

.modal-footer .btn-primary {
    background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
    color: white;
}

.modal-footer .btn-primary:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(40, 167, 69, 0.4);
}

.modal-footer .btn-secondary {
    background: #6c757d;
    color: white;
}

.modal-footer .btn-secondary:hover {
    background: #5a6268;
    transform: translateY(-1px);
}

/* Styles pour les onglets et tableaux */
.section-tabs {
    display: flex;
    border-bottom: 2px solid #e9ecef;
    margin-bottom: 24px;
}

.tab-button {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 20px;
    border: none;
    background: none;
    color: #6c757d;
    font-weight: 500;
    cursor: pointer;
    border-bottom: 3px solid transparent;
    transition: all 0.3s ease;
    flex: 1;
    justify-content: center;
}

.tab-button:hover {
    color: #28a745;
    background: rgba(40, 167, 69, 0.05);
}

.tab-button.active {
    color: #28a745;
    border-bottom-color: #28a745;
    background: rgba(40, 167, 69, 0.1);
}

.section-content {
    min-height: 300px;
}

.info-table {
    background: white;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.table-row {
    display: flex;
    border-bottom: 1px solid #e9ecef;
    transition: background-color 0.2s;
}

.table-row:last-child {
    border-bottom: none;
}

.table-row:hover {
    background: rgba(40, 167, 69, 0.02);
}

.table-row.full-width {
    flex-direction: column;
}

.table-label {
    flex: 0 0 200px;
    padding: 16px 20px;
    background: #f8f9fa;
    font-weight: 600;
    color: #495057;
    border-right: 1px solid #e9ecef;
    display: flex;
    align-items: center;
}

.table-value {
    flex: 1;
    padding: 16px 20px;
    color: #2c3e50;
    display: flex;
    align-items: center;
}

.table-value.salary {
    color: #28a745;
    font-weight: 600;
    font-size: 1.1rem;
}

.table-value.description {
    line-height: 1.5;
    padding: 20px;
}

.full-width .table-label {
    border-right: none;
    border-bottom: 1px solid #e9ecef;
    flex: none;
}

.full-width .table-value {
    padding-top: 0;
}

@media (max-width: 768px) {
    .modal-content {
        width: 95%;
        margin: 20px;
    }
    
    .modal-header,
    .modal-body,
    .modal-footer {
        padding: 20px;
    }
    
    .info-grid {
        grid-template-columns: 1fr;
        gap: 16px;
    }
    
    .contract-status {
        flex-direction: column;
        align-items: flex-start;
        gap: 12px;
    }
    
    .modal-footer {
        flex-direction: column;
    }
    
    .modal-footer .btn {
        width: 100%;
        justify-content: center;
    }
}

/* Pagination */
.pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 16px;
    margin-top: 32px;
}

.pagination-info {
    font-size: 14px;
    color: #6b7280;
}

/* Boutons */
.btn {
    padding: 8px 16px;
    border: none;
    border-radius: 8px;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;
    display: inline-flex;
    align-items: center;
    gap: 6px;
}

.btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.btn-primary {
    background: #059669;
    color: white;
}

.btn-primary:hover:not(:disabled) {
    background: #047857;
}

.btn-secondary {
    background: #f3f4f6;
    color: #374151;
}

.btn-secondary:hover:not(:disabled) {
    background: #e5e7eb;
}

.btn-sm {
    padding: 6px 12px;
    font-size: 12px;
}

/* Responsive */
@media (max-width: 768px) {
    .employees-container {
        padding: 16px;
    }

    .filters-grid {
        grid-template-columns: 1fr;
    }

    .employees-grid {
        grid-template-columns: 1fr;
    }

    .filters-actions {
        flex-direction: column;
        gap: 12px;
        align-items: stretch;
    }
}
</style>
