package aulab_chronicle.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import aulab_chronicle.models.CareerRequest;
import aulab_chronicle.models.Role;
import aulab_chronicle.models.User;
import aulab_chronicle.repositories.CareerRequestRepository;
import aulab_chronicle.repositories.ImageRepository;
import aulab_chronicle.repositories.RoleRepository;
import aulab_chronicle.repositories.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class CareerRequestServiceImpl implements CareerRequestService {

    @Autowired
    private CareerRequestRepository careerRequestRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    @Override
    // Evitiamo che un utente invii duplicati di una richiesta per un ruolo per cui
    // ha già fatto domanda.
    public boolean isRoleAlreadyAssigned(User user, CareerRequest careerRequest) {
        List<Long> allUserIds = careerRequestRepository.findAllUserIds();

        if (!allUserIds.contains(user.getId())) {
            return false;
        }

        List<Long> requests = careerRequestRepository.findByUserId(user.getId());
        return requests.stream().anyMatch(roleId -> roleId.equals(careerRequest.getRole().getId()));
    }

    @Override
    public void save(CareerRequest careerRequest, User user) {
        careerRequest.setUser(user);
        careerRequest.setIsChecked(false);
        careerRequestRepository.save(careerRequest);

        // Invio mail di notifica all'amministratore
        emailService.sendSimpleEmail(
                "adminAulabpost@admin.com",
                "Richiesta per ruolo: " + careerRequest.getRole().getName(),
                "C'è una nuova richiesta di collaborazione da parte di " + user.getUsername());
    }

    @Override
    public CareerRequest find(Long id) {
        return careerRequestRepository.findById(id).get();
    }

    @Transactional
    @Override
    public void careerAccept(Long requestId) {
        CareerRequest request = careerRequestRepository.findById(requestId).get();
        User user = request.getUser();
        Role role = request.getRole();

        List<Role> rolesUser = user.getRoles();
        Role newRole = roleRepository.findByName(role.getName());
        rolesUser.add(newRole);

        user.setRoles(rolesUser);
        userRepository.save(user);

        request.setIsChecked(true);
        careerRequestRepository.save(request);

        // Invio mail di conferma all'utente
        emailService.sendSimpleEmail(
                user.getEmail(),
                "Ruolo abilitato",
                "Ciao, la tua richiesta di collaborazione è stata accettata dalla nostra amministrazione.");
    }

    @Override
    public void careerReject(Long requestId) {
        careerRequestRepository.deleteById(requestId);
    }
}
