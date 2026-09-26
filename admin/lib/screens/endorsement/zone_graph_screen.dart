// admin/lib/screens/endorsement/zone_graph_screen.dart

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:graphview/GraphView.dart';
import 'package:shared/shared.dart';
import 'endorsement_moderation_screen.dart';
import '../../providers/service_providers.dart';

class ZoneGraphScreen extends ConsumerStatefulWidget {
  const ZoneGraphScreen({super.key});

  @override
  ConsumerState<ZoneGraphScreen> createState() => _ZoneGraphScreenState();
}

class _ZoneGraphScreenState extends ConsumerState<ZoneGraphScreen> {
  static const int _graphDepth = 3;

  EndorsementGraph? _graph;
  ZoneInsights? _insights;
  bool _isLoading = true;
  String? _errorMessage;
  int _flaggedPatternCount = 0;

  // graphview objects, built once per data change, not per build.
  Graph? _gvGraph;
  FruchtermanReingoldAlgorithm? _gvAlgorithm;
  Map<String, EndorsementGraphNode> _modelNodeMap = {};

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    if (!mounted) return;
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final service = ref.read(adminEndorsementServiceProvider);

      final results = await Future.wait([
        service.getZoneGraph(depth: _graphDepth),
        service.getZoneInsights(),
        service.getSuspiciousPatterns(),
      ]);

      if (!mounted) return;

      final graph = results[0] as EndorsementGraph;
      final insights = results[1] as ZoneInsights;
      final flagged = results[2] as List<SuspiciousEndorsement>;
      final built = _buildGraphViewGraph(graph);

      setState(() {
        _graph = graph;
        _insights = insights;
        _flaggedPatternCount = flagged.length;
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

  /// Builds a graphview Graph once per data change.
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

  @override
  Widget build(BuildContext context) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        // Left: graph (75%)
        Expanded(
          flex: 3,
          child: _buildGraphArea(),
        ),
        // Right: insights panel (25%)
        Expanded(
          flex: 1,
          child: _buildInsightsPanel(),
        ),
      ],
    );
  }

  Widget _buildGraphArea() {
    if (_isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    if (_errorMessage != null || _graph == null) {
      return _buildErrorState();
    }

    if (_graph!.nodes.isEmpty) {
      return _buildEmptyState();
    }

    return _buildGraph();
  }

  Widget _buildGraph() {
    final gvGraph = _gvGraph;
    final algorithm = _gvAlgorithm;
    if (gvGraph == null || algorithm == null || gvGraph.nodes.isEmpty) {
      return const Center(child: CircularProgressIndicator());
    }

    return Padding(
      padding: const EdgeInsets.all(16),
      child: InteractiveViewer(
        constrained: false,
        minScale: 0.3,
        maxScale: 3.0,
        boundaryMargin: const EdgeInsets.all(120),
        child: GraphView(
          graph: gvGraph,
          algorithm: algorithm,
          paint: Paint()
            ..color = AppColors.primaryTeal.withValues(alpha: 0.3)
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

  Widget _buildNodeWidget(EndorsementGraphNode node) {
    const radius = 22.0;
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
              color: AppColors.charcoal.withValues(alpha: 0.85),
              shape: BoxShape.circle,
              border: Border.all(color: Colors.white, width: 2),
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
                  fontSize: 14,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
          ),
          const SizedBox(height: 4),
          Container(
            constraints: const BoxConstraints(maxWidth: 70),
            padding: const EdgeInsets.symmetric(horizontal: 5, vertical: 1),
            decoration: BoxDecoration(
              color: AppColors.surfaceGrey,
              borderRadius: BorderRadius.circular(6),
            ),
            child: Text(
              node.displayName.split(' ').first,
              overflow: TextOverflow.ellipsis,
              style: GoogleFonts.openSans(
                color: AppColors.charcoal,
                fontSize: 10,
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

  Widget _buildInsightsPanel() {
    // Placeholder filled in next step with ClusterInsightsPanel.
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: const BoxDecoration(
        color: Colors.white,
        border: Border(
          left: BorderSide(color: AppColors.textGrey, width: 0.5),
        ),
      ),
      child: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Zone Insights',
              style: GoogleFonts.poppins(
                fontSize: 16,
                fontWeight: FontWeight.w600,
                color: AppColors.charcoal,
              ),
            ),
            const SizedBox(height: 16),
            if (_isLoading)
              const Center(child: CircularProgressIndicator())
            else if (_insights != null) ...[
              _buildInsightRow('Total Endorsements',
                  _insights!.totalEndorsements.toString()),
              _buildInsightRow('Total Users', _insights!.totalUsers.toString()),
              _buildInsightRow(
                  'Clusters', _insights!.clusterCount.toString()),
              _buildInsightRow('Largest Cluster',
                  _insights!.largestClusterSize.toString()),
              _buildInsightRow('Isolated Users',
                  _insights!.isolatedUserCount.toString()),
            ] else
              Text(
                'No insights available',
                style: GoogleFonts.openSans(
                  color: AppColors.textGrey,
                  fontSize: 13,
                ),
              ),

            const SizedBox(height: 24),
            const Divider(height: 1),
            const SizedBox(height: 16),

            // Flagged patterns button with badge
            SizedBox(
              width: double.infinity,
              child: OutlinedButton.icon(
              onPressed: () async {
                  await Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) =>
                          const EndorsementModerationScreen(),
                    ),
                  );
                  // Refresh the flagged count in case any were dismissed
                  if (mounted) _loadData();
                },
                icon: Icon(
                  Icons.flag_outlined,
                  size: 16,
                  color: _flaggedPatternCount > 0
                      ? AppColors.error
                      : AppColors.textGrey,
                ),
                label: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Flexible(
                      child: Text(
                        'View Flagged',
                        overflow: TextOverflow.ellipsis,
                        style: GoogleFonts.openSans(
                          color: _flaggedPatternCount > 0
                              ? AppColors.error
                              : AppColors.textGrey,
                          fontSize: 13,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                    if (_flaggedPatternCount > 0) ...[
                      const SizedBox(width: 6),
                      Container(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 6,
                          vertical: 2,
                        ),
                        decoration: BoxDecoration(
                          color: AppColors.error,
                          borderRadius: BorderRadius.circular(10),
                        ),
                        child: Text(
                          '$_flaggedPatternCount',
                          style: GoogleFonts.openSans(
                            color: Colors.white,
                            fontSize: 11,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ),
                    ],
                  ],
                ),
                style: OutlinedButton.styleFrom(
                  side: BorderSide(
                    color: _flaggedPatternCount > 0
                        ? AppColors.error
                        : AppColors.textGrey,
                  ),
                  padding: const EdgeInsets.symmetric(vertical: 10),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(10),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildInsightRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 6),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Expanded(
            child: Text(
              label,
              style: GoogleFonts.openSans(
                fontSize: 13,
                color: AppColors.textGrey,
              ),
            ),
          ),
          Text(
            value,
            style: GoogleFonts.poppins(
              fontSize: 16,
              fontWeight: FontWeight.w600,
              color: AppColors.primaryTeal,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildErrorState() {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.error_outline,
                size: 64, color: AppColors.error),
            const SizedBox(height: 16),
            Text(
              'Failed to load zone graph',
              style: GoogleFonts.poppins(
                fontSize: 18,
                fontWeight: FontWeight.w600,
                color: AppColors.charcoal,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              _errorMessage ?? 'Please try again later',
              textAlign: TextAlign.center,
              style: GoogleFonts.openSans(
                fontSize: 14,
                color: AppColors.textGrey,
              ),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: _loadData,
              style: ElevatedButton.styleFrom(
                backgroundColor: AppColors.primaryTeal,
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
              color: AppColors.textGrey.withValues(alpha: 0.4),
            ),
            const SizedBox(height: 16),
            Text(
              'No endorsements yet',
              style: GoogleFonts.poppins(
                fontSize: 18,
                fontWeight: FontWeight.w600,
                color: AppColors.charcoal,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              'Your zone has no endorsement activity to display.',
              textAlign: TextAlign.center,
              style: GoogleFonts.openSans(
                fontSize: 14,
                color: AppColors.textGrey,
                height: 1.4,
              ),
            ),
          ],
        ),
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