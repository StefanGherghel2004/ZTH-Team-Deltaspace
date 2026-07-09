package cli.backend.userinterface.menus;

public class StartMenu extends Menu{

    @Override
    public void showMenu(){
        String logo = """
██████╗ ███████╗██╗  ████████╗ █████╗ ███████╗██████╗  █████╗  ██████╗███████╗
██╔══██╗██╔════╝██║  ╚══██╔══╝██╔══██╗██╔════╝██╔══██╗██╔══██╗██╔════╝██╔════╝
██║  ██║█████╗  ██║     ██║   ███████║███████╗██████╔╝███████║██║     █████╗
██║  ██║██╔══╝  ██║     ██║   ██╔══██║╚════██║██╔═══╝ ██╔══██║██║     ██╔══╝
██████╔╝███████╗███████╗██║   ██║  ██║███████║██║     ██║  ██║╚██████╗███████╗
╚═════╝ ╚══════╝╚══════╝╚═╝   ╚═╝  ╚═╝╚══════╝╚═╝     ╚═╝  ╚═╝ ╚═════╝╚══════╝
""";
        System.out.println(logo);
        System.out.println("\n--- Welcome to Deltaspace platform ---");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Quit");
    }
}
