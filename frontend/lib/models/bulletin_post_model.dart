class BulletinPost {
  final String id;
  final String title;
  final String body;
  final String category;
  final String authorId;
  final String authorName;
  final String authorAvatar;
  final List<String> imageUrls;
  final int helpfulCount;
  final int commentCount;
  final DateTime createdAt;
  final bool isOwner;
  final bool isReported;
  final bool isHelpfulByUser;
  final bool isExpired;

  BulletinPost({
    required this.id,
    required this.title,
    required this.body,
    required this.category,
    required this.authorId,
    required this.authorName,
    required this.authorAvatar,
    this.imageUrls = const [],
    this.helpfulCount = 0,
    this.commentCount = 0,
    required this.createdAt,
    this.isOwner = false,
    this.isReported = false,
    this.isHelpfulByUser = false,
    this.isExpired = false,
  });

  // Mock data for now
  static List<BulletinPost> getMockPosts() {
    return [
      BulletinPost(
        id: '1',
        title: 'Lost my cat, Max',
        body: 'Last seen near Burnette Street. Please DM if found.',
        category: 'lost_pet',
        authorId: 'user_1',
        authorName: 'Sarah Johnson',
        authorAvatar: 'S',
        imageUrls: ['https://via.placeholder.com/150', 'https://via.placeholder.com/150'],
        helpfulCount: 12,
        commentCount: 5,
        createdAt: DateTime.now().subtract(const Duration(hours: 2)),
        isOwner: true,
      ),
      BulletinPost(
        id: '2',
        title: 'Neighbourhood Braai',
        body: 'This Saturday at 2pm! Bring your own meat and drinks, I am hosting a Braai. Kids welcome!, Message me for my address',
        category: 'local_event',
        authorId: 'user_2',
        authorName: 'Michael Jackson',
        authorAvatar: 'M',
        imageUrls: ['https://via.placeholder.com/150'],
        helpfulCount: 8,
        commentCount: 3,
        createdAt: DateTime.now().subtract(const Duration(days: 1)),
      ),
      BulletinPost(
        id: '3',
        title: 'Suspicious Activity Alert',
        body: 'A suspicious vehicle has been seen circling the area, bear Hilda street. Please lock doors and report anything unusual.',
        category: 'alert',
        authorId: 'user_3',
        authorName: 'Lisa Wong',
        authorAvatar: 'L',
        imageUrls: [],
        helpfulCount: 15,
        commentCount: 7,
        createdAt: DateTime.now().subtract(const Duration(days: 2)),
      ),
      BulletinPost(
        id: '4',
        title: 'Community Meeting',
        body: 'Join us this Sunday to plan the community garden. All welcome!',
        category: 'general',
        authorId: 'user_1',
        authorName: 'Sarah Johnson',
        authorAvatar: 'S',
        imageUrls: [],
        helpfulCount: 4,
        commentCount: 2,
        createdAt: DateTime.now().subtract(const Duration(days: 3)),
      ),
    ];
  }

  static String getCategoryLabel(String category) {
    switch (category) {
      case 'general':
        return 'General';
      case 'lost_pet':
        return 'Lost Pet';
      case 'local_event':
        return 'Local Event';
      case 'alert':
        return 'Alert';
      case 'free_items':
        return 'Free Items';
      case 'complaint':
        return 'Complaint';
      case 'admin':
        return 'Admin Announcement';
      default:
        return category;
    }
  }

  static String getCategoryColor(String category) {
    switch (category) {
      case 'general':
        return '#2A9D8F';
      case 'lost_pet':
        return '#F4A261';
      case 'local_event':
        return '#E9C46A';
      case 'alert':
        return '#F44336';
      case 'free_items':
        return '#69B578';
      case 'complaint':
        return '#9B59B6';
      case 'admin':
        return '#3498DB';
      default:
        return '#2A9D8F';
    }
  }
}