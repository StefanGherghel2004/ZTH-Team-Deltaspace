package cli.backend.duplicates;

import cli.backend.repositories.UserRepository;


public class UserDuplicate implements  CheckDuplicate{
    private static final int userColumn=1;

    @Override
    public boolean isDuplicate(String usernameOrEmail) {
        if(usernameOrEmail == null){
            return false;
        }

        return UserRepository.getInstance().findByUsernameOrEmail(usernameOrEmail).isPresent();
    }
}
