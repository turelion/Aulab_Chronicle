package aulab_chronicle.services;

import aulab_chronicle.models.CareerRequest;
import aulab_chronicle.models.User;

public interface CareerRequestService {
    boolean isRoleAlreadyAssigned(User user, CareerRequest careerRequest);
    void save(CareerRequest careerRequest, User user);
    void careerAccept(Long requestId);
    CareerRequest find(Long id);
    void careerReject(Long requestId);
}
