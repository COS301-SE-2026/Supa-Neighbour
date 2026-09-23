// frontend/lib/screens/endorsement/endorsement_graph_screen.dart

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:graphview/GraphView.dart';
import 'package:shared/shared.dart' hide AppColors;
import '../../constants/app_colors.dart';
import '../../providers/service_providers.dart';

class EndorsementGraphScreen extends ConsumerStatefulWidget {
  /// Optional user id. If null, shows the current user's own network.
  final String? userId;

  /// Optional display name, used in the app bar for other-user views.
  final String? userDisplayName;

  const EndorsementGraphScreen({
    super.key,
    this.userId,
    this.userDisplayName,
  });

  @override
  ConsumerState<EndorsementGraphScreen> createState() =>
      _EndorsementGraphScreenState();
}

class _EndorsementGraphScreenState
    extends ConsumerState<EndorsementGraphScreen> {
  static const int _graphDepth = 2;

  EndorsementGraph? _graph;
  List<SkillTag> _skillTags = [];
  String? _selectedSkillTag;
  bool _isLoading = true;
  String? _errorMessage;
  bool _isReloading = false;


  Graph? _gvGraph;
  FruchtermanReingoldAlgorithm? _gvAlgorithm;
  Map<String, EndorsementGraphNode> _modelNodeMap = {};

  @override
  void initState() {
    super.initState();
    _loadInitialData();
  }

  Future<void> _loadInitialData() async {
    if (!mounted) return;
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final service = ref.read(endorsementServiceProvider);

      final results = await Future.wait([
        service.getSkillTags(),
        service.getGraph(
          userId: widget.userId,
          depth: _graphDepth,
          skillTag: _selectedSkillTag,
        ),
      ]);

      if (!mounted) return;

      final graph = results[1] as EndorsementGraph;
      final built = _buildGraphViewGraph(graph);

      setState(() {
        _skillTags = results[0] as List<SkillTag>;
        _graph = graph;
        _gvGraph = built.graph;
        _gvAlgorithm = built.algorithm;
        _modelNodeMap = built.modelNodeMap;
        _isLoading = false;
      });
    } catch (e) {
      if (!mounted) return;
      setState(() {
        _errorMessage = e.toString().replaceFirst('Exception: ', '');
        _isLoading = false;
      });
    }
  }

  Future<void> _reloadGraph() async {
    if (!mounted) return;
    setState(() => _isReloading = true);

    try {
      final service = ref.read(endorsementServiceProvider);
      final graph = await service.getGraph(
        userId: widget.userId,
        depth: _graphDepth,
        skillTag: _selectedSkillTag,
      );

      if (!mounted) return;

      final built = _buildGraphViewGraph(graph);

      setState(() {
        _graph = graph;
        _gvGraph = built.graph;
        _gvAlgorithm = built.algorithm;
        _modelNodeMap = built.modelNodeMap;
        _isReloading = false;
      });
    } catch (e) {
      if (!mounted) return;
      setState(() => _isReloading = false);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            e.toString().replaceFirst('Exception: ', ''),
          ),
          backgroundColor: AppColors.error(context),
        ),
      );
    }
  }

  /// Builds a graphview Graph once per data change.
  /// Storing it in state avoids rebuilding on every widget rebuild,
  /// which prevents lifecycle races during disposal.
  _BuiltGraph _buildGraphViewGraph(EndorsementGraph graph) {
    final gvGraph = Graph();
    final nodeMap = <String, Node>{};
    final modelNodeMap = <String, EndorsementGraphNode>{};

    for (final node in graph.nodes) {
      final gvNode = Node.Id(node.userId);
      nodeMap[node.userId] = gvNode;
      modelNodeMap[node.userId] = node;
      gvGraph.addNode(gvNode);
    }

    for (final edge in graph.edges) {
      final from = nodeMap[edge.endorserId];
      final to = nodeMap[edge.endorseeId];
      if (from == null || to == null) continue;
      gvGraph.addEdge(from, to);
    }

    return _BuiltGraph(
      graph: gvGraph,
      algorithm: FruchtermanReingoldAlgorithm(
        FruchtermanReingoldConfiguration(),
      ),
      modelNodeMap: modelNodeMap,
    );
  }

  void _onSkillSelected(String? skillTag) {
    if (!mounted) return;
    setState(() => _selectedSkillTag = skillTag);
    _reloadGraph();
  }


  @override
  Widget build(BuildContext context) {
    final title = widget.userId == null
        ? 'My Trust Network'
        : '${widget.userDisplayName ?? 'User'}\'s Network';

    return Scaffold(
        backgroundColor: AppColors.background(context),
        appBar: AppBar(
        backgroundColor: AppColors.background(context),
        elevation: 0,
        leading: IconButton(
          icon: Icon(
            Icons.arrow_back,
            color: AppColors.primaryTeal(context),
          ),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          title,
          style: GoogleFonts.poppins(
            color: AppColors.charcoal(context),
            fontSize: 20,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
      ),
      body: Column(
        children: [
          _buildSkillFilterRow(),
          Expanded(child: _buildBody()),
          ],
        ),
    );
  }


  Widget _buildSkillFilterRow() {
    return Container(
      height: 50,
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: _skillTags.isEmpty
          ? const SizedBox.shrink()
          : ListView(
              scrollDirection: Axis.horizontal,
              children: [
                _buildFilterChip(
                  label: 'All',
                  isSelected: _selectedSkillTag == null,
                  onTap: () => _onSkillSelected(null),
                ),
                ..._skillTags.map((tag) {
                  return Padding(
                    padding: const EdgeInsets.only(left: 8),
                    child: _buildFilterChip(
                      label: tag.displayName,
                      isSelected: _selectedSkillTag == tag.tag,
                      onTap: () => _onSkillSelected(tag.tag),
                    ),
                  );
                }),
              ],
            ),
    );
  }

  Widget _buildFilterChip({
    required String label,
    required bool isSelected,
    required VoidCallback onTap,
  }) {
    return GestureDetector(
      onTap: onTap,
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 150),
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
        decoration: BoxDecoration(
          color: isSelected
              ? AppColors.primaryTeal(context)
              : Colors.transparent,
          borderRadius: BorderRadius.circular(20),
          border: Border.all(
            color: isSelected
                ? AppColors.primaryTeal(context)
                : AppColors.textGrey(context).withValues(alpha: 0.4),
          ),
        ),
        child: Text(
          label,
          style: GoogleFonts.openSans(
            color: isSelected
                ? Colors.white
                : AppColors.charcoal(context),
            fontSize: 13,
            fontWeight: isSelected ? FontWeight.w600 : FontWeight.w500,
          ),
        ),
      ),
    );
  }

  Widget _buildBody() {
    if (_isLoading || _isReloading) {
      return Center(
        child: CircularProgressIndicator(
          color: AppColors.primaryTeal(context),
        ),
      );
    }

    if (_errorMessage != null || _graph == null) {
      return _buildErrorState();
    }

    if (_graph!.nodes.isEmpty) {
      return _buildEmptyState();
    }

    return _buildGraphPlaceholder();
  }

  Widget _buildErrorState() {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.error_outline,
              size: 64,
              color: AppColors.error(context),
            ),
            const SizedBox(height: 16),
            Text(
              'Failed to load network',
              style: GoogleFonts.poppins(
                color: AppColors.charcoal(context),
                fontSize: 18,
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              _errorMessage ?? 'Please try again later',
              textAlign: TextAlign.center,
              style: GoogleFonts.openSans(
                color: AppColors.textGrey(context),
                fontSize: 14,
              ),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: _loadInitialData,
              style: ElevatedButton.styleFrom(
                backgroundColor: AppColors.primaryTeal(context),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
              ),
              child: Text(
                'Retry',
                style: GoogleFonts.openSans(color: Colors.white),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildEmptyState() {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.hub_outlined,
              size: 64,
              color: AppColors.textGrey(context).withValues(alpha: 0.4),
            ),
            const SizedBox(height: 16),
            Text(
              'No endorsements yet',
              style: GoogleFonts.poppins(
                color: AppColors.charcoal(context),
                fontSize: 18,
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              _selectedSkillTag == null
                  ? 'Complete tasks and get endorsed by neighbours to build your network.'
                  : 'No endorsements found for this skill.',
              textAlign: TextAlign.center,
              style: GoogleFonts.openSans(
                color: AppColors.textGrey(context),
                fontSize: 14,
                height: 1.4,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildGraphPlaceholder() {
    final gvGraph = _gvGraph;
    final algorithm = _gvAlgorithm;
    if (gvGraph == null || algorithm == null || gvGraph.nodes.isEmpty) {
      return const Center(child: CircularProgressIndicator());
    }

    return Padding(
      padding: const EdgeInsets.all(16),
      child: InteractiveViewer(
        constrained: false,
        minScale: 0.5,
        maxScale: 3.0,
        boundaryMargin: const EdgeInsets.all(80),
        child: GraphView(
          graph: gvGraph,
          algorithm: algorithm,
          paint: Paint()
            ..color = AppColors.primaryTeal(context).withValues(alpha: 0.3)
            ..strokeWidth = 1.5
            ..style = PaintingStyle.stroke,
          builder: (Node node) {
            final nodeId = node.key?.value as String?;
            final modelNode = _modelNodeMap[nodeId];
            if (modelNode == null) return const SizedBox.shrink();
            return _buildNodeWidget(modelNode);
          },
        ),
      ),
    );
  }

  /// Renders a single user node in the graph.
  Widget _buildNodeWidget(EndorsementGraphNode node) {
    final isCentre = node.isCentreNode;
    final radius = isCentre ? 32.0 : 24.0;
    final initial =
        node.displayName.isNotEmpty ? node.displayName[0].toUpperCase() : '?';

    return GestureDetector(
      onTap: () => _onNodeTapped(node),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Container(
            width: radius * 2,
            height: radius * 2,
            decoration: BoxDecoration(
              color: isCentre
                  ? AppColors.primaryTeal(context)
                  : AppColors.charcoal(context).withValues(alpha: 0.85),
              shape: BoxShape.circle,
              border: Border.all(
                color: isCentre
                    ? AppColors.primaryTeal(context)
                    : Colors.white,
                width: isCentre ? 3 : 2,
              ),
              boxShadow: [
                BoxShadow(
                  color: Colors.black.withValues(alpha: 0.1),
                  blurRadius: 6,
                  offset: const Offset(0, 2),
                ),
              ],
            ),
            child: Center(
              child: Text(
                initial,
                style: GoogleFonts.poppins(
                  color: Colors.white,
                  fontSize: isCentre ? 20 : 15,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
          ),
          const SizedBox(height: 6),
          Container(
            constraints: const BoxConstraints(maxWidth: 80),
            padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
            decoration: BoxDecoration(
              color: isCentre
                  ? AppColors.primaryTeal(context)
                  : AppColors.surfaceGrey(context),
              borderRadius: BorderRadius.circular(8),
            ),
            child: Text(
              isCentre ? 'You' : node.displayName.split(' ').first,
              overflow: TextOverflow.ellipsis,
              style: GoogleFonts.openSans(
                color: isCentre
                    ? Colors.white
                    : AppColors.charcoal(context),
                fontSize: 11,
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
        ],
      ),
    );
  }

  void _onNodeTapped(EndorsementGraphNode node) {
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text('Tapped: ${node.displayName}'),
        duration: const Duration(seconds: 1),
      ),
    );
  }
}

class _BuiltGraph {
  final Graph graph;
  final FruchtermanReingoldAlgorithm algorithm;
  final Map<String, EndorsementGraphNode> modelNodeMap;

  _BuiltGraph({
    required this.graph,
    required this.algorithm,
    required this.modelNodeMap,
  });
}